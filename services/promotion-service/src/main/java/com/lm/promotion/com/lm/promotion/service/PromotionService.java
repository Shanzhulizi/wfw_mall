package com.lm.promotion.com.lm.promotion.service;

import com.lm.promotion.dto.LockCouponsDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {

    BigDecimal lockAndCalc(LockCouponsDTO lockRequest);

    void confirmUseCoupons(Long userId, List<Long> lockedCouponIds, String orderNo);

    void releaseCoupons( List<Long> lockedCouponIds);

    void cancelLockCoupons(List<Long> couponUserIds);

    void releaseCouponsByOrderNo(String orderNo);
}
