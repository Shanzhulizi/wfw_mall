package com.lm.cart.domain;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class Cart {
    private Long skuId;//购物车一般只要一个具体的，不需要大类spu
    private String name;
    private String image;
    private BigDecimal price;
    private Integer count;
    private Boolean checked; // 是否勾选结算
}
