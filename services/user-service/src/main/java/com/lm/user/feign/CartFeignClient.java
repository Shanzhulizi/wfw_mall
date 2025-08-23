package com.lm.user.feign;

import com.lm.user.feign.fallback.CartFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "product-service",fallback = CartFeignClientFallback.class) // feign客户端
public interface CartFeignClient {

    //同步未登录状态的购物车到用户的购物车
}
