package com.lm.product.mapper;

import com.lm.product.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    @Select("SELECT * FROM product_image WHERE spu_id = #{spuId}")
    List<ProductImage> selectBySpuId(Long spuId);
}
