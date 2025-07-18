package com.lm.order.feign;


import com.lm.common.R;
import com.lm.order.feign.fallback.CouponFeignClientFallback;
import com.lm.promotion.dto.LockCouponsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "coupon-service", fallback = CouponFeignClientFallback.class) // feign客户端
public interface CouponFeignClient {

    @PostMapping("/coupon/lock")
    R lockCoupon(List<Long> couponUserIds);



}
