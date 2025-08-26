package com.lm.product.service.impl;

import com.lm.product.dto.*;
import com.lm.product.feign.UserFeignClient;
import com.lm.product.mapper.ProductMapper;
import com.lm.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.lm.common.constant.RedisConstants.STOCK_KEY_PREFIX;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public void preloadStockToRedis() {
        // 预热商品库存到Redis
        List<ProductPreloadDTO> products = productMapper.selectAllAvailable(); // 查询所有在售商品
        log.info("Preloading stock for {} products", products.size());
        for (ProductPreloadDTO product : products) {
            String key = STOCK_KEY_PREFIX + product.getId();
            stringRedisTemplate.opsForValue().set(key, product.getStock().toString());
        }
    }

    @Override
    public List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return productMapper.selectPriceValidationByIds(ids);

    }

    @Override
    public ProductCartDTO getProductById(Long skuId) {
        if (skuId == null) {
            log.warn("SKU ID is null, cannot fetch product details.");
            return null; // or throw an exception, or return a default value
        }
        ProductCartDTO productCartDTO = productMapper.selectProductById(skuId);
        if (productCartDTO != null) {
            log.info("Fetched product details for SKU ID: {}", skuId);
            return productCartDTO;
        } else {
            log.warn("No product found for SKU ID: {}", skuId);
        }
        return null;
    }

    @Override
    public List<ProductRecommendDTO> getRecommendedProducts(int page, int size) {
        int offset = (page - 1) * size;
        return productMapper.findRecommended(offset, size);
    }




//

    @Override
    public ProductCategoryDTO getCategoryById(Long id) {

        return productMapper.selectCategoryById(id);
    }

    @Override
    public ProductBrandDTO getBrandById(Long id) {

        return productMapper.selectBrandById(id);
    }

    @Override
    public List<ProductSkuDTO> getSkusBySpuId(Long spuId) {

        return productMapper.selectSkusBySpuId(spuId);
    }

    @Override
    public List<ProductSpuDTO> listSpus(Long lastUpdateTime) {
        List<ProductSpuDTO> spus;
        if (lastUpdateTime != null) {
            //解析时间戳
            Date date = new Date(lastUpdateTime);
            spus = productMapper.selectSpusAfterDate(date);
        } else {
            spus = productMapper.selectAllDate();
        }

        return spus;
    }

    @Override
    public List<ProductSpuDTO> getSpusUpdatedAfter(Date sinceTime, int page, int size) {
        int offset = (page - 1) * size;
        return productMapper.selectUpdatedAfterWithPagination(sinceTime, offset, size);

    }

    @Override
    public List<ProductSpuDTO> getAllSpus(int page, int size) {
        int offset = (page - 1) * size;
        return productMapper.selectAllWithPagination(offset, size);
    }

    @Override
    public ProductSpuDTO getSpuById(Long spuId) {
        ProductSpuDTO spu = productMapper.selectSpuById(spuId);

        return spu;
    }


}