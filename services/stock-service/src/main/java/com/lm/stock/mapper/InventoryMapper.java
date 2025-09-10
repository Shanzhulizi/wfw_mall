package com.lm.stock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InventoryMapper {

    // 预扣库存: 扣减可用库存，增加锁定库存
    int deductAvailableStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    // 实扣库存: 扣减锁定库存
    int reduceLockedStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    // 释放库存: 增加可用库存，扣减锁定库存
    int releaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}
