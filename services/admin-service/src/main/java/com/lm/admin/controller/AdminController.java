package com.lm.admin.controller;

import com.lm.admin.dto.AdminLoginDTO;
import com.lm.admin.service.AdminService;
import com.lm.common.R;
import com.lm.user.dto.DeleteUserDTO;
import com.lm.user.dto.UserLoginDTO;
import com.lm.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequestMapping("/admin")
@RestController
public class AdminController {


    @Autowired
    AdminService adminService;

    @PostMapping("/login")
    public R login(@RequestBody AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        R r = adminService.login(username, password);

        Integer statusCode = r.getCode();
        if (statusCode != 200) {
            log.info("管理员登录失败，用户名：{}，错误信息：{}", username, r.getMsg());
            return R.error(r.getMsg());
        }
        String token = r.getData().toString();
        return R.ok("登录成功", "Bearer " + token);
    }

    /**
     * 禁用管理员账户，但是需要提供禁用原因，要用请求体，所以用了Post
     *
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/ban")
    public R ban(@RequestBody DeleteUserDTO dto, HttpServletRequest request) {

        return null;
    }

    /**
     * 批准或拒绝商家申请
     */
    public R replyApplication(){

        return null;
    }

    /**
     * 发放优惠券
     */
    public R giveCoupon(){

        return null;
    }


    /**
     * 上架秒杀活动
     */
    public R SeckillActivity(){

        return null;
    }


    /**
     * 这里我的构想本来应该是管理员调用这个方法的，但是我想先在product服务里实现这个功能再说
     * @return
     */
    @GetMapping("/preloadStock")
    public R  preloadStockToRedis(){
//        productService.preloadStockToRedis();
//        return R.ok("商品库存预热完成");
        return null;
    }

}
