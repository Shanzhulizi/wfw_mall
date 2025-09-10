package com.lm.order.feign;


import com.lm.order.feign.fallback.ProductFeignClientFallback;
import com.lm.product.vo.ProductSkuVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "product-service",fallback = ProductFeignClientFallback.class) // feign客户端
public interface ProductFeignClient {



    //mvc注解的两套使用逻辑
    //1、标注在Controller上，是接受这样的请求
    //2、标注在FeignClient上，是发送这样的请求



    @GetMapping("/product/skuInfo")
    ProductSkuVO getSkuInfo(@RequestParam("skuId") Long skuId);
}
