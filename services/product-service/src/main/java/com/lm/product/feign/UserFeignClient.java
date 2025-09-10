package com.lm.product.feign;


import com.lm.common.R;
import com.lm.product.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", path = "/merchant" , fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    /**
     * 根据ID获取商家信息
     */
    @GetMapping("/{id}")
    R getMerchantById(@PathVariable("id") Long id);

    /**
     * 批量获取商家信息
     */
    @PostMapping("/batch")
    R getMerchantsByIds(@RequestBody List<Long> ids);
}