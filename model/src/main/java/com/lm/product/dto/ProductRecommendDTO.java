package com.lm.product.dto;

import lombok.Data;

@Data
public class ProductRecommendDTO {
    Long id;
    String name;
    String description; // 商品描述
    String mainImage;
    Integer saleCount;  //销量
}
