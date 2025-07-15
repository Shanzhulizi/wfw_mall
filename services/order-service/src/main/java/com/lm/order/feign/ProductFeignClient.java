package com.lm.order.feign;


import com.lm.order.feign.fallback.ProductFeignClientFallback;
import com.lm.product.dto.ProductDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "product-service",fallback = ProductFeignClientFallback.class) // feign客户端
public interface ProductFeignClient {



    //mvc注解的两套使用逻辑
    //1、标注在Controller上，是接受这样的请求
    //2、标注在FeignClient上，是发送这样的请求



    @PostMapping("/product/getByIds")
    List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(@RequestBody List<Long> ids);
}
