package com.lm.order.feign.fallback;

import com.lm.order.feign.ProductFeignClient;
import com.lm.product.vo.ProductSkuVO;

public class ProductFeignClientFallback implements ProductFeignClient {


    @Override
    public ProductSkuVO getSkuInfo(Long skuId) {
        return null;
    }
}
