package com.lm.product.service;

import com.lm.product.dto.ProductCartDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import com.lm.product.dto.ProductRecommendDTO;

import java.util.List;

public interface ProductService {
    void preloadStockToRedis();

    List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(List<Long> ids);

    ProductCartDTO getProductById(Long skuId);


    List<ProductRecommendDTO> getRecommendedProducts(int page, int size);
}
