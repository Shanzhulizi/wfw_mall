package com.lm.user.service.impl;

import com.lm.common.R;
import com.lm.common.utils.JwtUtils;
import com.lm.user.domain.User;
import com.lm.user.domain.UserDeleteLog;
import com.lm.user.dto.UserUpdateDTO;
import com.lm.user.mapper.UserDeleteMapper;
import com.lm.user.mapper.UserMapper;
import com.lm.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserDeleteMapper userDeleteMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public R loginWithPasswordOrCode(String phone, String password, String code) {

        if (phone == null || phone.isEmpty()) {
            return R.error("手机号不能为空");
        }
        if ((password == null || password.isEmpty()) && (code == null || code.isEmpty())) {
            return R.error("请填写密码或验证码");
        }
        User loginUser = null;
        if (password == null || password.isEmpty()) {
            // 手机号验证码登录
            // 从 Redis 中获取验证码
            String redisCode = stringRedisTemplate.opsForValue().get("register:code:" + phone);
            if (redisCode == null) {
                return R.error("验证码已过期或不存在");
            }
            // 验证码匹配
            if (!redisCode.equals(code)) {
                return R.error("验证码不正确");
            }
            // 验证通过后，删除 Redis 中的验证码
            stringRedisTemplate.delete("register:code:" + phone);

            loginUser = userMapper.selectByPhone(phone);

        } else {
            // 手机号密码登录
            loginUser = userMapper.selectByPhone(phone);
            if (loginUser == null) {
                return R.error("用户不存在，请先注册");
            }
            // 用 BCrypt 检查密码是否匹配
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matched = passwordEncoder.matches(password, loginUser.getPassword());

            if (!matched) {
                return R.error("密码错误");
            }

            //TODO 添加ThreadLocal
            //这里还要添加token(jwt)
            //添加ThreadLocal，添加登录自动过期功能

            // 登录成功后，设置用户信息到 ThreadLocal

        }
        //无论何种方式，只要通过都要返回token
        //jwt作为token
        String token = getToken(loginUser);
        log.info("创建账户成功，手机号：{}，生成的token：{}", phone, "Bearer " + token);
        if (token == null || token.isEmpty()) {
            return R.error("创建token失败，请稍后再试");
        }

        return R.ok("登录成功",  token);

    }


    @Override
    public R createAccountOrLoginWithPhone(String phone) {
        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        User user = userMapper.selectByPhone(phone);

        if (user == null) {
            //如果用户不存在，则创建新账户
            int result = userMapper.insertAccountWithPhone(phone, now);
            log.info("创建账户，手机号：{}，结果：{}", phone, result);
            if (result <= 0) {
                log.info("创建账户失败，手机号：{}", phone);
                return R.error("创建账户失败，请稍后再试");
            }
            user = userMapper.selectByPhone(phone);
        } else {
            //直接登录

        }

        String token = getToken(user);
        log.info("创建账户成功，手机号：{}，生成的token：{}", phone, "Bearer " + token);
        if (token == null || token.isEmpty()) {
//            throw new RuntimeException("创建token失败，请稍后再试");
            return R.error("创建token失败，请稍后再试");
        }


        return R.ok("账户创建或登录成功", "Bearer " + token);
    }

    private static String getToken(User user) {
        //jwt作为token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user"); // 角色可以是 user 或 admin，根据实际情况设置
        claims.put("id", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("userType", user.getUserType());

        String token = JwtUtils.generateToken(claims);
        return token;
    }


    @Override
    public R changeAccountInfo(String username, String password, String email, Long userId) {

        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        UserUpdateDTO user = new UserUpdateDTO();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(email);

        // 插入用户信息到数据库
        int effects = userMapper.updateUserByDTO(user);

        if (effects <= 0) {
            log.info("更新用户信息失败，用户ID：{}", userId);
            return R.error("更新用户信息失败，请稍后再试");
        }
        log.info("用户信息更新成功，用户ID：{}", userId);
        return R.ok("用户信息更新成功");
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    /**
     * 这得是一个事务，要么一起成功，要么一起失败
     */
    public void deleteUser(Long userId, String deleteReason, String ip, String userAgent, String phone, int userType) {

        //TODO 这里还得检查是不是商户，如果商户注销，merchant表也要受影响
        if (userType == 2) {
            log.info("商户注销");
            //TODO 商户注销逻辑,我感觉会很复杂，还要记录注销商户表
        }
        int deleteNum = userMapper.deleteUserById(userId);
        if (deleteNum != 1) {
            throw new RuntimeException("删除用户失败");
        }

        LocalDateTime deleteTime = LocalDateTime.now();

        UserDeleteLog userDeleteLog = new UserDeleteLog();
        userDeleteLog.setUserId(userId);
        userDeleteLog.setDeleteReason(deleteReason);
        userDeleteLog.setIp(ip);
        userDeleteLog.setUserAgent(userAgent);
        userDeleteLog.setPhone(phone);
        userDeleteLog.setDeleteTime(deleteTime);
        // 插入注销日志
        log.info("用户注销，用户ID：{}，删除原因：{}，IP：{}，User-Agent：{}，手机号：{}，用户类型：{}",
                userId, deleteReason, ip, userAgent, phone, userType);
        int insertNum = userDeleteMapper.insertDeleteRecord(userDeleteLog);
        if (insertNum != 1) {
            throw new RuntimeException("插入注销日志失败");
        }


    }


}
