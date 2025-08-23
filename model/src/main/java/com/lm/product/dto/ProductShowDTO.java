package com.lm.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductShowDTO {
    Long id;
    String name;
    String description; // 商品描述
    String mainImage;
    Integer saleCount;  //销量
    BigDecimal price;
    String merchantName; // 商户名称
}
