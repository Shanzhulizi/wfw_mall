package com.lm.order.feign.fallback;

import com.lm.common.R;
import com.lm.order.feign.CouponFeignClient;
import com.lm.promotion.dto.LockCouponsDTO;

import java.math.BigDecimal;
import java.util.List;

public class CouponFeignClientFallback implements CouponFeignClient {


    @Override
    public BigDecimal lockAndCalc(LockCouponsDTO lockRequest) {
        return BigDecimal.ZERO;
    }

    @Override
    public R confirmUseCoupons(Long userId, List<Long> lockedCouponIds, String orderNo) {
        return null;
    }

    @Override
    public R releaseCoupons(List<Long> lockedCouponIds) {
        return null;
    }

    @Override
    public void cancelLockCoupons(List<Long> couponUserIds) {

    }

    @Override
    public R releaseCouponsByOrderNo(String orderNo) {
        return null;
    }
}
