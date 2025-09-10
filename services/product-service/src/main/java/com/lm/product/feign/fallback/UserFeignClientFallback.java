package com.lm.product.feign.fallback;


import com.lm.common.R;
import com.lm.product.feign.UserFeignClient;

import java.util.List;

public class UserFeignClientFallback implements UserFeignClient {


    @Override
    public R getMerchantById(Long id) {
        return null;
    }

    @Override
    public R getMerchantsByIds(List<Long> ids) {
        return null;
    }
}
