package com.lm.promotion.com.lm.promotion.service;

import java.util.List;

public interface PromotionService {
    void lockCoupon(List<Long> couponUserIds);
}
