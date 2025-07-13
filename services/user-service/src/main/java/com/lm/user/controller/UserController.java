package com.lm.user.controller;


import com.lm.common.R;
import com.lm.user.domain.User;
import com.lm.user.dto.RegisterDTO;
import com.lm.user.service.UserService;
import com.lm.user.utils.VertifyCodeUtil;
import com.lm.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public R login(@RequestParam String username, @RequestParam String password) {
        log.info("User login attempt with username: {}", username);

        // 格式交给前端校验
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return R.error("Username and password cannot be empty");
        }

        try {
            User user = userService.login(username, password);
            return R.ok("Login successful", user);
        } catch (Exception e) {
            log.info("Login failed for user {}: {}", username, e.getMessage());
            return R.error(e.getMessage());
        }

    }

    @PostMapping("/register")
    public R register(@RequestParam String phone) {
//        log.info("User registration attempt with username: {}", registerDTO.getUsername());

        //调用第三方服务发送验证码
        //我没钱，所以我把它放到了model里面
        // 怎么调用呢？
        String code = new VertifyCodeUtil().sendVerificationCode(phone);
        if (code == null) {
            return R.error("验证码发送失败，请稍后再试");
        }
        log.info("验证码发送成功，手机号：{}，验证码：{}", phone, code);
        // 将验证码存入 Redis，设置过期时间为5分钟
        stringRedisTemplate.opsForValue()
                .set("register:code:" + phone, code, 5 * 60, TimeUnit.SECONDS);

        return R.ok("验证码发送成功");
    }

    @PostMapping("/register/verify")
    public R verify(@RequestParam String code, @RequestParam String phone) {
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

        // 没有则向数据库插入用户记录，有的话直接登录
        R result = userService.createAccountOrLoginWithPhone(phone);
        if (result.getCode().equals(200)) {
            String token = (String) result.getData();
            return R.ok("注册登录成功", token);
        } else {
            // 如果返回结果是错误的，直接返回错误信息
            log.info("注册登录失败，手机号：{}，错误信息：{}", phone, result.getMsg());
            return R.error(result.getMsg());
        }

    }

    @PostMapping("/registerInfo")
    public R UserInfo(String username, String password,  String email) {

        Long userId = UserContextHolder.getUser().getId();
        log.info("用户id:{}",userId);
        R r = userService.changeAccountInfo(username, password, email, userId);
        if( r.getCode().equals(200)) {
            return R.ok("信息补充完成");
        } else {
            log.info("信息补充失败，用户ID：{}，错误信息：{}", userId, r.getMsg());
            return R.error(r.getMsg());
        }
    }


}
