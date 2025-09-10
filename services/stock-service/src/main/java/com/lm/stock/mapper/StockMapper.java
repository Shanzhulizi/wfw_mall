package com.lm.stock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface StockMapper {

    // 扣减库存（乐观锁：只要 stock >= quantity 且 version 匹配才能成功）
    @Update("UPDATE product_sku " +
            "SET stock = stock - #{quantity}, version = version + 1 " +
            "WHERE id = #{skuId} AND stock >= #{quantity} AND version = version")
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    // 回滚库存
    @Update("UPDATE product_sku " +
            "SET stock = stock + #{quantity}, version = version + 1 " +
            "WHERE id = #{skuId}")
    int restoreStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}

