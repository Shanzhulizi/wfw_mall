package com.lm.user.service.impl;

import com.lm.common.R;
import com.lm.common.utils.JwtUtils;
import com.lm.order.dto.ReceiverInfoDTO;
import com.lm.user.domain.User;
import com.lm.user.domain.UserDeleteLog;
import com.lm.user.dto.UserInfoDTO;
import com.lm.user.dto.UserUpdateDTO;
import com.lm.user.feign.CouponFeignClient;
import com.lm.user.mapper.UserDeleteMapper;
import com.lm.user.mapper.UserMapper;
import com.lm.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserDeleteMapper userDeleteMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CouponFeignClient couponFeignClient;

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
            String redisCode = stringRedisTemplate.opsForValue().get("login:code:" + phone);
            if (redisCode == null) {
                return R.error("验证码已过期或不存在");
            }
            // 验证码匹配
            if (!redisCode.equals(code)) {
                return R.error("验证码不正确");
            }
            // 验证通过后，删除 Redis 中的验证码
            stringRedisTemplate.delete("login:code:" + phone);

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
        //TODO 通过后把购物车的内容同步到用户的购物车
        //调用购物车服务


        //无论何种方式，只要通过都要返回token
        //jwt作为token
        String token = getToken(loginUser);

// 这里我们用 userId 作为 key，存 token 作为 value
        String key = "login:token:" + loginUser.getId();

// 存入 Redis，并设置过期时间，比如 30 分钟
        stringRedisTemplate.opsForValue().set(key, token, 30, TimeUnit.MINUTES);
        log.info("创建token成功，手机号：{}，生成的token：{}", phone, token);
        if (token == null || token.isEmpty()) {
            return R.error("创建token失败，请稍后再试");
        }

        return R.ok("登录成功", token);

    }


    @Override
    public R createAccountWithPhone(String phone) {
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
        } else {
            return R.error("账户已存在，请直接登录");
        }


        log.info("创建账户成功，手机号：{}", phone);


        return R.ok("账户创建成功", phone);
    }

    private static String getToken(User user) {
        //jwt作为token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user"); // 角色可以是 user 或 admin，根据实际情况设置
        claims.put("id", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("userType", user.getUserType());

        String token = JwtUtils.generateToken(claims);
        return "Bearer " + token;
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


    @Override
    public boolean isLogin(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        try {
            // 验证token有效性
            Claims claims = JwtUtils.parseToken(token.replace("Bearer ", ""));
            Long userId = Long.valueOf(claims.get("id").toString());

//            // 可选：检查token是否在黑名单中（如退出登录的token）
//            if (stringRedisTemplate.hasKey("blacklist:" + token)) {
//                return false;
//            }

            // 可选：检查用户状态是否正常
            User user = userMapper.getById(userId);
            if (user == null) {
                return false;
//                return R.error("用户不存在或已被禁用");
            }

            return true;

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("token已过期");
        } catch (Exception e) {
            throw new RuntimeException("token无效");
        }
    }

    @Override
    public void logout(Long userId) {
        String key = "login:token:" + userId;
        stringRedisTemplate.delete(key);
    }

    @Override
    public String loginAfterRegisterSuccess(String phone) {
        User user = userMapper.selectByPhone(phone);

        String token = getToken(user);

        stringRedisTemplate.opsForValue().set("login:token:" + user.getId(), token, 30, TimeUnit.MINUTES);

        return token;
    }

    @Override
    public UserInfoDTO getUserInfo(Long userId) {

        //从user表获取
        UserInfoDTO userInfo = userMapper.getUserInfoById(userId);


//        //调用promotion-service获取
//        int couponCount = 0; // 假设从promotion-service获取的优惠券数量
//        Object data = couponFeignClient.getCouponCountByUserId(userId).getData();
//
//        couponCount = data == null ? 0 : Integer.parseInt(data.toString());
//
//        userInfo.setCouponCount(couponCount);

        return userInfo;
    }



    public List<ReceiverInfoDTO> getReceiverInfoByUserId(Long userId){
        return userMapper.getReceiverInfoByUserId(userId);
    }
}
