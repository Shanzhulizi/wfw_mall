package com.lm.product.mapper;

import com.lm.product.domain.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BrandMapper {
    @Select("SELECT * FROM product_brand WHERE id = #{brandId}")
    Brand selectById(Long brandId);
}
