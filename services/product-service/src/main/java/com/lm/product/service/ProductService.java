package com.lm.product.service;

import com.lm.product.dto.ProductCartDTO;
import com.lm.product.dto.ProductPriceValidationDTO;

import java.util.List;

public interface ProductService {
    void preloadStockToRedis();

    List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(List<Long> ids);

    ProductCartDTO getProductById(Long skuId);
}
