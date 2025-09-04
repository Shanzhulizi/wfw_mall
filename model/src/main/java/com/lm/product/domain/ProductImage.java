package com.lm.product.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ProductImage {
    private Long id;
    private Long spuId;
    private String imageUrl;
    private Integer sortOrder;
}