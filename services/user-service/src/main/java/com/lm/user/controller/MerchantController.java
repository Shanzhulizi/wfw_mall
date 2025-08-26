package com.lm.user.controller;


import com.lm.common.R;
import com.lm.user.domain.Merchant;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.dto.AuditDTO;
import com.lm.user.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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




    @GetMapping("/{id}")
    public R getMerchantById(@PathVariable Long id) {
        // 实现获取商家信息逻辑
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            return R.error("商家不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", merchant.getId());
        result.put("shopName", merchant.getShopName());
        result.put("phone", merchant.getPhone());
        result.put("status", merchant.getStatus());
        // 其他需要返回的字段...

        return R.ok("",result);
    }

    @PostMapping("/batch")
    public R getMerchantsByIds(@RequestBody List<Long> ids) {
        // 实现批量获取商家信息逻辑
        List<Merchant> merchants = merchantService.listByIds(ids);
        List<Map<String, Object>> result = merchants.stream()
                .map(merchant -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", merchant.getId());
                    map.put("shopName", merchant.getShopName());
                    map.put("phone", merchant.getPhone());
                    map.put("status", merchant.getStatus());
                    return map;
                })
                .collect(Collectors.toList());

        return R.ok("",result);
    }

}
