package com.lm.user.controller;


import com.lm.common.R;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.dto.AuditDTO;
import com.lm.user.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    /**
     * 申请称为商家
     *
     * @return
     */
    // 用户提交申请
    @PostMapping("/apply")
    public R applyMerchant(@RequestBody MerchantApplication application) {
        merchantService.apply(application);
        return R.ok("申请提交成功，等待审核");
    }



    @GetMapping("/application/getByStatus")
    public List<MerchantApplication> getApplicationsByStatus(@RequestParam("status") int status) {
        return merchantService.listByStatus(status);
    }
    @GetMapping("/application/getReviewed")
    List<MerchantApplication> getApplicationsReviewed() {
        return merchantService.listReviewed();
    }

    @GetMapping("/application/getAll")
    List<MerchantApplication> getApplicationsAll() {
        return merchantService.listAll();
    }

    @PostMapping("/application/audit")
    void audit(@RequestBody AuditDTO auditDTO) {

        Long id = auditDTO.getId();
        Integer status = auditDTO.getStatus();
        String reason = auditDTO.getReason();
        log.info("审核商家申请，id: {}, status: {}, reason: {}", id, status, reason);
        merchantService.audit(id, status, reason);
    }

}
