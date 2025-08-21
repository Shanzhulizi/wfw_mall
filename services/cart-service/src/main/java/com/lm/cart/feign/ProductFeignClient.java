package com.lm.cart.feign;

import com.lm.cart.feign.fallback.ProductFeignClientFallback;
import com.lm.product.dto.ProductCartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "product-service",fallback = ProductFeignClientFallback.class) // feign客户端
public interface ProductFeignClient {
    @GetMapping("/product/{skuId}")
    ProductCartDTO getProductById(@PathVariable("skuId") Long skuId);


}
