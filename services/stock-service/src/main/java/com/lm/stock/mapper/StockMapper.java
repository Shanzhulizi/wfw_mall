package com.lm.stock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.Mapping;



@Mapper
public interface StockMapper {
    @Update("UPDATE product_sku SET stock = stock - #{quantity} " +
            "WHERE id = #{skuId} AND stock >= #{quantity}")
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

}
