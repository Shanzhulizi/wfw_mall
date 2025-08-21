package com.lm.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCartDTO {
    private Long skuId;//购物车一般只要一个具体的，不需要大类spu
    private String skuName;//商品名
    private String image;
    private BigDecimal price;
    private Integer count;
}
