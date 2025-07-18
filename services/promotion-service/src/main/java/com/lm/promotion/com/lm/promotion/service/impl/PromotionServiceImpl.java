package com.lm.promotion.com.lm.promotion.service.impl;

import com.lm.promotion.com.lm.promotion.mapper.CouponUserMapper;
import com.lm.promotion.com.lm.promotion.mapper.PromotionMapper;
import com.lm.promotion.com.lm.promotion.service.PromotionService;
import com.lm.promotion.domain.CouponUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    CouponUserMapper couponUserMapper;

    @Override
    public void lockCoupon(List<Long> couponUserIds) {

        for (Long couponUserId : couponUserIds) {
            // 查询当前 coupon_user
            CouponUser couponUser = couponUserMapper.selectById(couponUserId);

            // 乐观锁更新
            int updateCount = couponUserMapper.updateStatusToUsed(
                    couponUser.getId(),
                    couponUser.getVersion()
            );

            if (updateCount == 0) {
                throw new RuntimeException("优惠券已被使用");
            }
        }



    }
}
