package com.lm.product.mapper;

import com.lm.product.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
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




    List<ProductRecommendDTO> findRecommended(int offset, int size);








    @Select("SELECT * FROM product_category WHERE id = #{id}")
    ProductCategoryDTO selectCategoryById(Long id);

    @Select("SELECT * FROM product_brand WHERE id = #{id}")
    ProductBrandDTO selectBrandById(Long id);

    @Select("SELECT * FROM product_sku WHERE spu_id = #{spuId}")
    List<ProductSkuDTO> selectSkusBySpuId(Long spuId);

    @Select("SELECT * FROM product_spu WHERE update_time > #{lastUpdateTime} ")
    List<ProductSpuDTO> selectSpusAfterDate(Date date);

    @Select("SELECT * FROM product_spu")
    List<ProductSpuDTO> selectAllDate();


    @Select("SELECT * FROM product_spu WHERE status = 1 ORDER BY id LIMIT #{offset}, #{size}")
    List<ProductSpuDTO> selectAllWithPagination(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT * FROM product_spu WHERE update_time >= #{sinceTime} AND status = 1 ORDER BY update_time LIMIT #{offset}, #{size}")
    List<ProductSpuDTO> selectUpdatedAfterWithPagination(@Param("sinceTime") Date sinceTime,
                                                      @Param("offset") int offset,
                                                      @Param("size") int size);

    @Select("SELECT * FROM product_spu WHERE id = #{spuId}")
    ProductSpuDTO selectSpuById(Long spuId);
}
