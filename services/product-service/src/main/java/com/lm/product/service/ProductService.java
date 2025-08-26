package com.lm.product.service;

import com.lm.product.dto.*;

import java.util.Date;
import java.util.List;

public interface ProductService {
    void preloadStockToRedis();

    List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(List<Long> ids);

    ProductCartDTO getProductById(Long skuId);


    List<ProductRecommendDTO> getRecommendedProducts(int page, int size);




    ProductCategoryDTO getCategoryById(Long id);

    ProductBrandDTO getBrandById(Long id);

    List<ProductSkuDTO> getSkusBySpuId(Long spuId);


    List<ProductSpuDTO> listSpus(Long lastUpdateTime);

    List<ProductSpuDTO> getSpusUpdatedAfter(Date sinceTime, int page, int size);

    List<ProductSpuDTO> getAllSpus(int page, int size);

    ProductSpuDTO getSpuById(Long spuId);
}
