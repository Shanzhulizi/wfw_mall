package com.lm.promotion.com.lm.promotion.service.impl;

import com.lm.promotion.com.lm.promotion.mapper.CouponMapper;
import com.lm.promotion.com.lm.promotion.mapper.CouponUserMapper;
import com.lm.promotion.com.lm.promotion.service.PromotionService;
import com.lm.promotion.domain.Coupon;
import com.lm.promotion.domain.CouponUser;
import com.lm.promotion.dto.LockCouponsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    CouponUserMapper couponUserMapper;


    @Override
    @Transactional
    public void confirmUseCoupons(Long userId, List<Long> lockedCouponIds, String orderNo) {
        for (Long couponUserId : lockedCouponIds) {
            int rows = couponUserMapper.confirmCoupon(couponUserId, userId, orderNo);
            if (rows == 0) {
                throw new RuntimeException("优惠券确认失败: " + couponUserId);
            }
        }
    }


    @Transactional
    @Override
    public void releaseCoupons(List<Long> lockedCouponIds) {
        for (Long couponUserId : lockedCouponIds) {
            int rows = couponUserMapper.releaseCoupon(couponUserId);
            if (rows == 0) {
                throw new RuntimeException("优惠券确认失败: " + couponUserId);
            }
        }
    }

    @Override
    public void cancelLockCoupons(List<Long> couponUserIds) {
        for (Long couponUserId : couponUserIds) {
            int rows = couponUserMapper.cancelLockCoupon(couponUserId);
            if (rows == 0) {
                throw new RuntimeException("取消锁定优惠券失败: " + couponUserId);
            }
        }
    }

    @Override
    public void releaseCouponsByOrderNo(String orderNo) {
        int rows = couponUserMapper.releaseCouponByOrderNo(orderNo);
        if (rows == 0) {
            throw new RuntimeException("根据订单号释放优惠券失败: ");
        }

    }

    @Transactional
    @Override
    public BigDecimal lockAndCalc(LockCouponsDTO lockRequest) {
        List<Long> couponIds = lockRequest.getCouponUserIds();
        if (couponIds == null || couponIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 1. 查询用户券详情
        List<CouponUser> coupons = couponUserMapper.findByIdsAndUser(couponIds, lockRequest.getUserId());
        if (coupons.size() != couponIds.size()) {
            throw new RuntimeException("部分优惠券不存在或不属于当前用户");
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;

        // 2. 遍历锁券并计算优惠金额
        for (CouponUser couponUser : coupons) {
            // 校验有效期
            if (couponUser.getExpireTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("优惠券已过期: " + couponUser.getId());
            }


            // 2.1 尝试锁券（更新 status=0 → status=1）
            int updated = couponUserMapper.lockCoupon(
                    couponUser.getId(),
                    lockRequest.getUserId()
            );

            if (updated == 0) {
                throw new RuntimeException("优惠券已被使用或锁定: " + couponUser.getId());
            }

            // 2.2 计算优惠金额
            Coupon coupon = couponMapper.findById(couponUser.getCouponId());
            BigDecimal discount = calcDiscount(couponUser, coupon, lockRequest.getOrderAmount());
            totalDiscount = totalDiscount.add(discount);
        }

        return totalDiscount;
    }

    private BigDecimal calcDiscount(CouponUser couponUser, Coupon coupon, BigDecimal orderAmount) {
        // 1. 校验有效期
        if (couponUser.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期: " + couponUser.getId());
        }

        if (orderAmount.compareTo(coupon.getMinAmount()) < 0) {
            throw new RuntimeException("订单金额未达到优惠券使用门槛: " + couponUser.getId());
        }

        // 2. 根据券类型计算优惠
        BigDecimal discount = BigDecimal.ZERO;
        switch (coupon.getType()) {
            case 1: // 满减/现金券
                discount = coupon.getDiscountAmount();
                break;

            case 2: // 折扣券
                discount = orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()));
                break;

            case 3: // 现金券（直接抵扣固定金额）
                discount = coupon.getDiscountAmount();
                break;

            default:
                throw new RuntimeException("未知的优惠券类型: " + coupon.getType());
        }

        // 3. 限制优惠金额不能超过订单金额
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }

        return discount;
    }


}
