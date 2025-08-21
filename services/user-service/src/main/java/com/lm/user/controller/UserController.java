package com.lm.user.controller;


import com.lm.common.R;
import com.lm.order.dto.ReceiverInfoDTO;
import com.lm.user.dto.DeleteUserDTO;
import com.lm.user.dto.UserLoginDTO;
import com.lm.user.mapper.ReceiverMapper;
import com.lm.user.service.UserService;
import com.lm.user.utils.VertifyCodeUtil;
import com.lm.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    ReceiverMapper receiverMapper;

    @PostMapping("/login")
    public R login(@RequestBody UserLoginDTO userLoginDTO) {
        String phone = userLoginDTO.getPhone();
        String password = userLoginDTO.getPassword();
        String code = userLoginDTO.getCode();
        R r = userService.loginWithPasswordOrCode(phone, password, code);

        Integer statusCode = r.getCode();
        if (statusCode != 200) {
            log.info("登录失败，手机号：{}，错误信息：{}", phone, r.getMsg());
            return R.error(r.getMsg());
        }
        String token = r.getData().toString();

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

    //上传头像
    public R uploadAvatar() {


        return null;
    }



    //TODO

    /**
     * 购买会员
     *
     * @return
     */
    public R buyVip() {

        return null;
    }


    //----下面是外部调用------------------------------

    @GetMapping("/receiverInfo/verify-address")
    boolean verifyAddressBelongsToUser(
            @RequestParam("userId") Long userId, @RequestParam("receiverInfoId") Long receiverInfoId) {
        if (userId == null || receiverInfoId == null) {
            return false;
        }
        // 查询数据库，检查地址是否存在且属于该用户
        int count = receiverMapper.countByUserIdAndReceiverInfoId(userId, receiverInfoId);
        return count > 0;
    }

    @GetMapping("/receiverInfo/getById")
    ReceiverInfoDTO getReceiveInfoBy(@RequestParam Long receiverInfoId) {
        if (receiverInfoId == null) {
            return null;
        }
        // 查询数据库，获取收货地址信息
        ReceiverInfoDTO receiverInfo = receiverMapper.getReceiverInfoById(receiverInfoId);
        if (receiverInfo == null) {
            return null; // 或者抛出异常
        }
        return receiverInfo;
    }


}
