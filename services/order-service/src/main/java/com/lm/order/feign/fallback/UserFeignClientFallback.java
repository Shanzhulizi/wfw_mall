package com.lm.order.feign.fallback;

import com.lm.order.feign.UserFeignClient;

public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public boolean verifyAddressBelongsToUser(Long userId, Long receiverInfoId) {
        // 返回默认值或抛出异常
        return false; // 或者抛出自定义异常
    }
}
