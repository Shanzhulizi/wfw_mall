package com.lm.admin.controller;

import com.lm.admin.dto.AdminLoginDTO;
import com.lm.admin.service.AdminService;
import com.lm.admin.service.MerchantApplicationService;
import com.lm.common.R;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.dto.AuditDTO;
import com.lm.user.dto.DeleteUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequestMapping("/admin")
@RestController
public class AdminController {


    @Autowired
    AdminService adminService;
    @Autowired
    private MerchantApplicationService merchantApplicationService;

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
     * 列举所有审核中申请
     */
    @GetMapping("/applications/pending")
    public List<MerchantApplication> listPending() {
        return merchantApplicationService.listByStatus(0);
    }

    /**
     * 列举所有 审核完成 的申请（通过 + 拒绝）
     * 去用户服务查询
     *
     * @return
     */
    @GetMapping("/applications/reviewed")
    public List<MerchantApplication> listReviewed() {
        return merchantApplicationService.listReviewed();
    }

    /**
     * 列举 所有申请
     *
     * @return
     */
    @GetMapping("/applications/all")
    public List<MerchantApplication> listAll() {
        return merchantApplicationService.listAll();
    }

    /**
     * 批准或拒绝商家申请
     */
    @PostMapping("/application/audit")
    public R replyApplication(@RequestBody AuditDTO auditDTO) {
        Long id = auditDTO.getId();
        Integer status = auditDTO.getStatus();
        String reason = auditDTO.getReason();
        merchantApplicationService.audit(id, status, reason);
//        adminService.audit(id, status, reason);
        return R.ok("审核完成");
    }

    /**
     * 发放优惠券
     */
    public R giveCoupon() {

        return null;
    }


    /**
     * 上架秒杀活动
     */
    public R SeckillActivity() {

        return null;
    }


    /**
     * 这里我的构想本来应该是管理员调用这个方法的，但是我想先在product服务里实现这个功能再说
     *
     * @return
     */
    @GetMapping("/preloadStock")
    public R preloadStockToRedis() {
//        productService.preloadStockToRedis();
//        return R.ok("商品库存预热完成");
        return null;
    }

}
