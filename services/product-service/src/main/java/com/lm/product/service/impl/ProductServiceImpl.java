package com.lm.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.lm.common.R;
import com.lm.product.domain.*;
import com.lm.product.dto.*;
import com.lm.product.feign.UserFeignClient;
import com.lm.product.mapper.*;
import com.lm.product.service.ProductService;
import com.lm.product.vo.ProductDetailVO;
import com.lm.product.vo.ProductSkuVO;
import com.lm.user.domain.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ProductSpuMapper productSpuMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

//    @Autowired
//    private MerchantMapper merchantMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

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


    /**
     * 根据SPU ID获取商品详情（不含评论）
     */
    public ProductDetailVO getProductDetailById(Long spuId) {
        // 1. 获取SPU基本信息
        ProductSpu productSpu = productSpuMapper.selectById(spuId);
        if (productSpu == null || productSpu.getStatus() != 1) {
            return null;
        }

        // 2. 构建商品详情VO对象
        ProductDetailVO productDetail = new ProductDetailVO();
        BeanUtils.copyProperties(productSpu, productDetail);

        // 3. 获取分类名称
        Category category = categoryMapper.selectById(productSpu.getCategoryId());
        if (category != null) {
            productDetail.setCategoryName(category.getName());
        }

        // 4. 获取品牌名称
        Brand brand = brandMapper.selectById(productSpu.getBrandId());
        if (brand != null) {
            productDetail.setBrandName(brand.getName());
        }

        // 5. 获取店铺名称
        R r = userFeignClient.getMerchantById(productSpu.getMerchantId());
        Merchant merchant = (Merchant) r.getData();
//        Merchant merchant = merchantMapper.selectById(productSpu.getMerchantId());
        if (merchant != null) {
            productDetail.setShopName(merchant.getShopName());
        }

        // 6. 获取SKU信息
        List<ProductSku> skus = productSkuMapper.selectBySpuId(spuId);
        productDetail.setSkus(convertToSkuVOs(skus));

        // 7. 获取商品图片（从SKU中提取或单独查询）
        productDetail.setImages(getProductImages(spuId, skus));

        return productDetail;
    }

    @Override
    public ProductSkuVO getSkuInfo(Long skuId) {
        ProductSkuVO sku = productSkuMapper.getSkuById(skuId);
        if (sku == null) {
            throw new RuntimeException("商品不存在或已下架，skuId=" + skuId);
        }
        return sku;
    }

    /**
     * 转换SKU信息为VO对象
     */
    private List<ProductSkuVO> convertToSkuVOs(List<ProductSku> skus) {
        if (CollectionUtils.isEmpty(skus)) {
            return new ArrayList<>();
        }

        return skus.stream().map(sku -> {
            ProductSkuVO skuVO = new ProductSkuVO();
            BeanUtils.copyProperties(sku, skuVO);

            // 解析属性值JSON
            if (StringUtils.isNotBlank(sku.getAttrValueJson())) {
                try {
//                    Map<String, String> attrMap = JSON.parseObject(sku.getAttrValueJson(),
//                            new TypeReference<Map<String, String>>() {
//                            });
                    skuVO.setAttrValueJson(sku.getAttrValueJson());
                } catch (Exception e) {
                    log.warn("解析SKU属性JSON失败: {}", sku.getAttrValueJson(), e);
                }
            }

            return skuVO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取商品图片
     */
    private List<String> getProductImages(Long spuId, List<ProductSku> skus) {
        List<String> images = new ArrayList<>();
        ProductSpu productSpu = productSpuMapper.selectById(spuId);

        // 1. 添加主图
        if (StringUtils.isNotBlank(productSpu.getMainImage())) {
            images.add(productSpu.getMainImage());
        }

        // 2. 从SKU中获取图片
        skus.stream()
                .filter(sku -> StringUtils.isNotBlank(sku.getImage()))
                .map(ProductSku::getImage)
                .distinct()
                .forEach(images::add);

        // 3. 从商品图片表中获取额外图片
        List<ProductImage> extraImages = productImageMapper.selectBySpuId(spuId);
        extraImages.stream()
                .map(ProductImage::getImageUrl)
                .forEach(images::add);

        return images;
    }

}