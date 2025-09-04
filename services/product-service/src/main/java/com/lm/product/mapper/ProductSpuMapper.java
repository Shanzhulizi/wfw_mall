package com.lm.product.mapper;

import com.lm.product.domain.ProductSpu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductSpuMapper {

    @Select("SELECT * FROM product_spu WHERE id = #{spuId}")
    ProductSpu selectById(Long spuId);
}
