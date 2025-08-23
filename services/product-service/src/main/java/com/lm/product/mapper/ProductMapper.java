package com.lm.product.mapper;

import com.lm.es.ESProduct;
import com.lm.product.dto.ProductCartDTO;
import com.lm.product.dto.ProductPreloadDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import com.lm.product.dto.ProductRecommendDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductPreloadDTO> selectAllAvailable();


    /**
     * 根据商品ID列表查询商品价格校验信息
     * @param ids
     * @return
     */
    List<ProductPriceValidationDTO> selectPriceValidationByIds(List<Long> ids);

    ProductCartDTO selectProductById(Long skuId);

    @Select("""
        SELECT spu.id AS id, spu.name, spu.category_id AS categoryId,
               spu.brand_id AS brandId, spu.merchant_id AS merchantId,
               spu.description, spu.main_image AS mainImage,
               spu.status, spu.is_hot AS isHot, spu.is_new AS isNew,
               spu.is_recommended AS isRecommended, spu.sale_count AS saleCount,
               MIN(sku.price) AS minPrice,
               MAX(sku.price) AS maxPrice,
               SUM(sku.stock) AS totalStock
        FROM product_spu spu
        LEFT JOIN product_sku sku ON spu.id = sku.spu_id
        GROUP BY spu.id, spu.name, spu.category_id, spu.brand_id,
                 spu.merchant_id, spu.description, spu.main_image,
                 spu.status, spu.is_hot, spu.is_new, spu.is_recommended, spu.sale_count
    """)
    List<ESProduct> listAllForSearch();



    List<ProductRecommendDTO> findRecommended(int offset, int size);
}
