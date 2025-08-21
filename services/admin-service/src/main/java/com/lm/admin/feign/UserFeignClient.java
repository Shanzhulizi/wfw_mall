package com.lm.admin.feign;


import com.lm.admin.feign.fallback.UserFeignClientFallback;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.dto.AuditDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service", fallback = UserFeignClientFallback.class) // feign客户端
public interface UserFeignClient {



    @GetMapping("/merchant/application/getByStatus")
    List<MerchantApplication> getApplicationsByStatus(@RequestParam("status") int status);

    @GetMapping("/merchant/application/getReviewed")
    List<MerchantApplication> getApplicationsReviewed();

    @GetMapping("/merchant/application/getAll")
    List<MerchantApplication> getApplicationsAll();
    @PostMapping("/merchant/application/audit")
    void audit(@RequestBody AuditDTO auditDTO);
}
