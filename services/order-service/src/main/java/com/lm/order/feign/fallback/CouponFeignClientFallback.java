package com.lm.order.feign.fallback;

import com.lm.common.R;
import com.lm.order.feign.CouponFeignClient;

import java.util.List;

public class CouponFeignClientFallback implements CouponFeignClient {


    @Override
    public R lockCoupon(List<Long> couponUserIds) {

        return null;
    }
}
