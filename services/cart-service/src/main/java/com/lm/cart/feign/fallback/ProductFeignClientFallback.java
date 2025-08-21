package com.lm.cart.feign.fallback;

import com.lm.cart.feign.ProductFeignClient;
import com.lm.product.dto.ProductCartDTO;

public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public ProductCartDTO getProductById(Long skuId) {
        return null;
    }
}
