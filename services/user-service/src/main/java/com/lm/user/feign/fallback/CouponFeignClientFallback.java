package com.lm.user.feign.fallback;

import com.lm.common.R;
import com.lm.user.feign.CouponFeignClient;

public class CouponFeignClientFallback implements CouponFeignClient {


    @Override
    public R getCouponCountByUserId(Long userId) {
        return null;
    }
}
