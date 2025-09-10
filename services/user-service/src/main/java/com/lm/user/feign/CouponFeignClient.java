package com.lm.user.feign;


import com.lm.common.R;
import com.lm.user.feign.fallback.CouponFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "promotion-service", fallback = CouponFeignClientFallback.class) // feign客户端
public interface CouponFeignClient {


    @GetMapping("/promotion/coupon/count")
     R getCouponCountByUserId(@RequestParam("userId") Long userId);
}
