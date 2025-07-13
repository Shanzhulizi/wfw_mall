package com.lm.user.service.impl;

import com.lm.common.R;
import com.lm.user.domain.User;
import com.lm.user.dto.RegisterDTO;
import com.lm.user.dto.UserDTO;
import com.lm.user.dto.UserUpdateDTO;
import com.lm.user.mapper.UserMapper;
import com.lm.user.service.UserService;
import com.lm.user.utils.JwtUtil;
import com.lm.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User login(String username, String password) {

//        密码加密

        User user = userMapper.selectByUsername(username);
        if (user == null || user.getIsDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }
// 用 BCrypt 检查密码是否匹配
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matched = passwordEncoder.matches(password, user.getPassword());

        if (!matched) {
            throw new RuntimeException("密码错误");
        }

        //TODO 添加ThreadLocal
        //这里还要添加token(jwt)
        //添加ThreadLocal，添加登录自动过期功能

        return user;
    }

    @Override
    public R createAccountOrLoginWithPhone(String phone) {
        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        UserDTO userDTO = userMapper.selectByPhone(phone);

        if (userDTO == null) {
            //如果用户不存在，则创建新账户
            int result = userMapper.insertAccountWithPhone(phone, now);
            log.info("创建账户，手机号：{}，结果：{}", phone, result);
            if (result <= 0) {
                log.info("创建账户失败，手机号：{}", phone);
                return R.error("创建账户失败，请稍后再试");
            }
            userDTO = userMapper.selectByPhone(phone);
        } else {
            //直接登录

        }

        //jwt作为token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDTO.getId());
        claims.put("phone", userDTO.getPhone());
//        claims.put("username", null);//这里还没有填写用户名
        claims.put("userType", userDTO.getUserType());

        String token = JwtUtil.generateToken(claims);
        log.info("创建账户成功，手机号：{}，生成的token：{}", phone, "Bearer " + token);
        if (token == null || token.isEmpty()) {
//            throw new RuntimeException("创建token失败，请稍后再试");
            return R.error("创建token失败，请稍后再试");
        }

        return R.ok("账户创建或登录成功", "Bearer " + token);
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

        return R.ok("用户信息更新成功");
    }


}
