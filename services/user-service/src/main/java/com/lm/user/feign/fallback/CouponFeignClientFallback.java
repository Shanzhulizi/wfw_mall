package com.lm.user.feign.fallback;

import com.lm.common.R;
import com.lm.user.feign.CouponFeignClient;

import java.awt.color.ICC_Profile;
import java.util.List;

public class CouponFeignClientFallback implements CouponFeignClient {


    @Override
    public R getCouponCountByUserId(Long userId) {
        return null;
    }
}
