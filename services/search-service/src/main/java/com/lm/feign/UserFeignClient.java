package com.lm.feign;


import com.lm.common.R;
import com.lm.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/merchant" , fallback = UserFeignClientFallback.class)
public interface UserFeignClient {



    @GetMapping("/{id}")
    R getMerchantById(@PathVariable Long id);
}