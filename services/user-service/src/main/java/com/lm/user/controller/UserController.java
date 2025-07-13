package com.lm.user.controller;


import com.lm.common.R;
import com.lm.user.domain.User;
import com.lm.user.dto.DeleteUserDTO;
import com.lm.user.dto.RegisterDTO;
import com.lm.user.dto.UserLoginDTO;
import com.lm.user.mapper.UserMapper;
import com.lm.user.service.UserService;
import com.lm.user.utils.JwtUtil;
import com.lm.user.utils.VertifyCodeUtil;
import com.lm.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

    @Autowired
    UserMapper userMapper;

    @PostMapping("/login")
    public R login(@RequestBody UserLoginDTO userLoginDTO) {
        //TODO 登录功能 要么用手机号和验证码登录（已实现），要么用手机号和密码登录（未实现）

        String phone = userLoginDTO.getPhone();
        String password = userLoginDTO.getPassword();
        String code = userLoginDTO.getCode();
        if (phone == null || phone.isEmpty()) {
            return R.error("手机号不能为空");
        }
        if ((password == null || password.isEmpty()) && (code == null || code.isEmpty())) {
            return R.error("错误");
        }
        User loginUser = null;
        if (password == null || password.isEmpty()) {
            // 手机号验证码登录
            if (code == null || code.isEmpty()) {
                return R.error("验证码不能为空");
            }
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

            loginUser = userService.login(phone, password);

            if (loginUser == null) {
                return R.error("登录失败，手机号或密码错误");
            }

            // 登录成功后，设置用户信息到 ThreadLocal

        }

        //无论何种方式，只要通过都要返回token


        //jwt作为token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", loginUser.getId());
        claims.put("phone", loginUser.getPhone());
        claims.put("userType", loginUser.getUserType());

        String token = JwtUtil.generateToken(claims);
        log.info("创建账户成功，手机号：{}，生成的token：{}", phone, "Bearer " + token);
        if (token == null || token.isEmpty()) {
//            throw new RuntimeException("创建token失败，请稍后再试");
            return R.error("创建token失败，请稍后再试");
        }

        return R.ok("登录成功", "Bearer " + token);

    }

    @PostMapping("/register")
    public R register(@RequestParam String phone) {
//        log.info("User registration attempt with username: {}", registerDTO.getUsername());

        //TODO 调用第三方服务发送验证码
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
    public R UserInfo(@RequestParam String username, @RequestParam String password, @RequestParam String email) {

        Long userId = UserContextHolder.getUser().getId();
        log.info("用户id:{}", userId);
        R r = userService.changeAccountInfo(username, password, email, userId);
        if (r.getCode().equals(200)) {
            return R.ok("信息补充完成");
        } else {
            log.info("信息补充失败，用户ID：{}，错误信息：{}", userId, r.getMsg());
            return R.error(r.getMsg());
        }
    }

    /**
     * 虽然是删除用户，但是需要前端的用户提供删除原因，要用请求体，所以用了Post
     *
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public R deleteUser(@RequestBody DeleteUserDTO dto, HttpServletRequest request) {
        String deleteReason = dto.getDeleteReason();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Long userId = UserContextHolder.getUser().getId();
        String phone = UserContextHolder.getUser().getPhone();
        int userType = UserContextHolder.getUser().getUserType();
        try {
            userService.deleteUser(userId, deleteReason, ip, userAgent, phone, userType);
            return R.ok("用户删除成功");
        } catch (Exception e) {
            log.error("删除失败，用户ID：{}", userId, e);
            return R.error("删除失败，原因：" + e.getMessage());
        }

    }
}
