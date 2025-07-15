package com.lm.admin.service.impl;

import com.lm.admin.domain.Admin;
import com.lm.admin.mapper.AdminMapper;
import com.lm.admin.service.AdminService;
import com.lm.common.R;

import com.lm.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;


    @Override
    public R login(String username, String password) {
        if (password == null || password.isEmpty()) {
            return R.error("请填写密码");
        }
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            return R.error("用户不存在");
        }
        if( admin.getStatus() == 0) {
            return R.error("用户已被禁用");
        }
        // 用 BCrypt 检查密码是否匹配
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matched = passwordEncoder.matches(password, admin.getPassword());

        if (!matched) {
            return R.error("密码错误");
        }

        //TODO 添加ThreadLocal
        //这里还要添加token(jwt)
        //添加ThreadLocal，添加登录自动过期功能

        // 登录成功后，设置用户信息到 ThreadLocal


        //jwt作为token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin"); // 角色可以是 user 或 admin，根据实际情况设置
        claims.put("id", admin.getId());
        claims.put("permission", admin.getPermission());

        String token = JwtUtils.generateToken(claims);

        log.info("生成的token: {}", token);
        if (token == null || token.isEmpty()) {
            return R.error("创建token失败，请稍后再试");
        }

        return R.ok("登录成功", token);
    }
}
