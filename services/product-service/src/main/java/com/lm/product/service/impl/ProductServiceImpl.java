package com.lm.product.service.impl;

import com.lm.product.dto.ProductCartDTO;
import com.lm.product.dto.ProductPreloadDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import com.lm.product.mapper.ProductMapper;
import com.lm.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.lm.common.constant.RedisConstants.STOCK_KEY_PREFIX;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


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
}
