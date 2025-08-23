package com.lm.feign;


import com.lm.es.ESProduct;
import com.lm.feign.fallback.ProductFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "product-service",fallback = ProductFeignClientFallback.class) // feign客户端
public interface ProductFeignClient {


    @GetMapping("/product/listAllForSearch")
    public List<ESProduct> listAllForSearch() ;
}

