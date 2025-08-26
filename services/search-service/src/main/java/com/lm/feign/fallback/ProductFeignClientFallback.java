package com.lm.feign.fallback;


import com.lm.common.R;
import com.lm.feign.ProductFeignClient;

public class ProductFeignClientFallback implements ProductFeignClient {


    @Override
    public R listSpus(Long lastUpdateTime) {
        return null;
    }

    @Override
    public R listSpus(Long lastUpdateTime, int page, int size) {
        return null;
    }

    @Override
    public R getCategoryById(Long id) {
        return null;
    }

    @Override
    public R getBrandById(Long id) {
        return null;
    }

    @Override
    public R getSkusBySpuId(Long spuId) {
        return null;
    }

    @Override
    public R getSpuById(Long spuId) {
        return null;
    }
}
