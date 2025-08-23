package com.lm.feign.fallback;


import com.lm.es.ESProduct;
import com.lm.feign.ProductFeignClient;

import java.util.List;

public class ProductFeignClientFallback implements ProductFeignClient {


    @Override
    public List<ESProduct> listAllForSearch() {
        return null;
    }
}
