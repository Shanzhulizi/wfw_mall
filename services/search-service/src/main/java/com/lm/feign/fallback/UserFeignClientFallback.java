package com.lm.feign.fallback;


import com.lm.common.R;
import com.lm.feign.UserFeignClient;

import java.util.List;

public class UserFeignClientFallback implements UserFeignClient {


    @Override
    public R getMerchantById(Long id) {
        return null;
    }
}
