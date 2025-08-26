package com.lm.feign;


import com.lm.common.R;
import com.lm.feign.fallback.UserFeignClientFallback;
import com.lm.product.dto.MerchantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", path = "/merchant" , fallback = UserFeignClientFallback.class)
public interface UserFeignClient {



    @GetMapping("/{id}")
    R getMerchantById(@PathVariable Long id);
}