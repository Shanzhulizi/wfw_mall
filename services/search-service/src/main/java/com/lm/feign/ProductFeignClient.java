package com.lm.feign;


import com.lm.common.R;
import com.lm.feign.fallback.ProductFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", path = "/product" ,fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {


    @GetMapping("/spu/list")
    R listSpus(@RequestParam(required = false) Long lastUpdateTime);
    @GetMapping("/spu/list")
    R listSpus(@RequestParam(required = false) Long lastUpdateTime,
               @RequestParam(defaultValue = "1") int page,
               @RequestParam(defaultValue = "100") int size);

    @GetMapping("/category/{id}")
    R getCategoryById(@PathVariable Long id);

    @GetMapping("/brand/{id}")
    R getBrandById(@PathVariable Long id);

    @GetMapping("/sku/bySpu/{spuId}")
    R getSkusBySpuId(@PathVariable Long spuId);

    @GetMapping("/spu/{spuId}")
    R getSpuById(Long spuId);
}