package com.lm.promotion.com.lm.promotion.controller;

import com.lm.common.R;
import com.lm.promotion.com.lm.promotion.service.PromotionService;
import com.lm.promotion.dto.LockCouponsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
