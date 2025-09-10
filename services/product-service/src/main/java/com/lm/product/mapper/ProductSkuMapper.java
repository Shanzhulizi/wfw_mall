package com.lm.product.mapper;

import com.lm.product.domain.ProductSku;
import com.lm.product.vo.ProductSkuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper {

    @Select("SELECT * FROM product_sku WHERE spu_id = #{spuId}")
    List<ProductSku> selectBySpuId(Long spuId);


    @Select("SELECT id, spu_id AS spuId, sku_name AS skuName, price, stock, image, " +
            "attr_value_json AS attrValueJson, create_time AS createTime, update_time AS updateTime " +
            "FROM product_sku WHERE id = #{skuId}")
    ProductSkuVO getSkuById(@Param("skuId") Long skuId);
}
