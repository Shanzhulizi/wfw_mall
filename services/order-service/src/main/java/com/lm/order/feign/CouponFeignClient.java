package com.lm.order.feign;


import com.lm.common.R;
import com.lm.order.feign.fallback.CouponFeignClientFallback;
import com.lm.promotion.dto.LockCouponsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "promotion-service", fallback = CouponFeignClientFallback.class) // feign客户端
public interface CouponFeignClient {

    @PostMapping("/promotion/coupon/lock")
    BigDecimal lockAndCalc(@RequestBody LockCouponsDTO lockRequest);


    @PostMapping("/promotion/coupon/confirm")
    R confirmUseCoupons(@RequestParam Long userId,
                        @RequestParam List<Long> lockedCouponIds,
                        @RequestParam String orderNo);

    @PostMapping("/promotion/coupon/release")
    R releaseCoupons(   @RequestParam List<Long> lockedCouponIds);

    @PostMapping("/promotion/coupon/cancelLockCoupons")
    void cancelLockCoupons( @RequestBody  List<Long> couponUserIds);

    @PostMapping("/promotion/coupon/releaseByOrderNo")
    R releaseCouponsByOrderNo( @RequestParam  String orderNo);
}
