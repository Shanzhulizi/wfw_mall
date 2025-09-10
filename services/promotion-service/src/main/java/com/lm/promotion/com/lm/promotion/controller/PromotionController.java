package com.lm.promotion.com.lm.promotion.controller;

import com.lm.common.R;
import com.lm.promotion.com.lm.promotion.service.PromotionService;
import com.lm.promotion.dto.LockCouponsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequestMapping("/promotion")
@ResponseBody
@Controller
public class PromotionController {
    @Autowired
    PromotionService promotionService;

    //添加优惠券


    //删除优惠券


    /**
     * 锁定优惠券并计算优惠金额
     */
    @PostMapping("/coupon/lock")
    BigDecimal lockAndCalc(@RequestBody LockCouponsDTO lockRequest) {
        try {
            return promotionService.lockAndCalc(lockRequest);
        } catch (Exception e) {
            log.error("锁定优惠券失败，错误信息：{}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }


    /**
     * 确认使用优惠券
     */
    @PostMapping("/coupon/confirm")
    public R confirmUseCoupons(@RequestParam Long userId,
                               @RequestParam List<Long> lockedCouponIds,
                               @RequestParam String orderNo) {
        try {
            promotionService.confirmUseCoupons(userId, lockedCouponIds, orderNo);
            return R.ok("优惠券确认成功");
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }


    /**
     * 释放锁定的优惠券
     */
    @PostMapping("/coupon/release")
    R releaseCoupons(@RequestParam List<Long> lockedCouponIds) {
        try {
            promotionService.releaseCoupons(lockedCouponIds);
            return R.ok("释放优惠券成功");
        } catch (Exception e) {
            return R.error("释放优惠券失败: " + e.getMessage());
        }

    }



    @PostMapping("/coupon/cancelLockCoupons")
    void cancelLockCoupons( @RequestBody  List<Long> couponUserIds){
        try   {
            promotionService.cancelLockCoupons(couponUserIds);

        }catch (Exception e){
            throw new RuntimeException("释放优惠券失败: " + e.getMessage());
        }
    }

    @PostMapping("/coupon/releaseByOrderNo")
    R releaseCouponsByOrderNo( @RequestParam  String orderNo){
        try   {
            promotionService.releaseCouponsByOrderNo(orderNo);
            return R.ok("释放优惠券成功");
        }catch (Exception e){
            return R.error("释放优惠券失败: " + e.getMessage());
        }
    }
}
