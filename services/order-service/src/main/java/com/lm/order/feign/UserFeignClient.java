package com.lm.order.feign;


import com.lm.order.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", fallback = UserFeignClientFallback.class) // feign客户端

public interface UserFeignClient {


    /**
     * 校验地址正确性
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/receiverInfo/verify-address")
    boolean verifyAddressBelongsToUser(
            @RequestParam("userId") Long userId, @RequestParam("receiverInfoId") Long receiverInfoId);

}
