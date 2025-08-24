package com.lm.promotion.com.lm.promotion.controller;

import com.lm.common.R;
import com.lm.promotion.com.lm.promotion.service.PromotionService;
import feign.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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


    //锁定并校验优惠券

    @PostMapping("/coupon/lock")
    R lockCoupon(@RequestBody List<Long> couponUserIds) {


        try {
            promotionService.lockCoupon(couponUserIds);
            return R.ok();
        } catch (Exception e) {
            return R.error();
        }
    }

    //获取某用户的优惠券数量
    @GetMapping("/coupon/count")
    public R getCouponCountByUserId(@RequestParam("userId") Long userId) {
        int count =                promotionService.getCouponCountByUserId(userId);
        if (count < 0) {
            return R.error("查询失败");
        }
        return R.ok("", count);
    }

}
