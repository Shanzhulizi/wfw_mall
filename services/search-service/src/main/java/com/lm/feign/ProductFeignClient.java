package com.lm.feign;


import com.lm.common.R;
import com.lm.es.domain.ESProduct;
import com.lm.feign.fallback.ProductFeignClientFallback;
import com.lm.product.domain.PageResult;
import com.lm.product.dto.ProductBrandDTO;
import com.lm.product.dto.ProductCategoryDTO;
import com.lm.product.dto.ProductSkuDTO;
import com.lm.product.dto.ProductSpuDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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