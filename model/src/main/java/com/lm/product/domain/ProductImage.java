package com.lm.product.domain;

import lombok.Data;

@Data
public class ProductImage {
    private Long id;
    private Long spuId;
    private String imageUrl;
    private Integer sortOrder;
}