package com.lm.order.feign.fallback;

import com.lm.order.feign.ProductFeignClient;
import com.lm.product.dto.ProductPriceValidationDTO;

import java.util.Collections;
import java.util.List;

public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(List<Long> ids) {
        List<ProductPriceValidationDTO> emptyList = Collections.emptyList();
        return emptyList;
    }


}
