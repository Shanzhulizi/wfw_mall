package com.lm.product.mapper;

import com.lm.product.domain.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper {

    @Select("SELECT * FROM product_sku WHERE spu_id = #{spuId}")
    List<ProductSku> selectBySpuId(Long spuId);
}
