package com.lm.cart.mapper;

import com.lm.cart.dto.CartDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper {
    CartDTO queryCartBySkuId(Long skuId);







}
