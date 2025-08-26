package com.lm.product.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProductSpuDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private Long brandId;
    private Long merchantId;
    private String description;
    private String mainImage;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommended;
    private Integer saleCount;
    private Date createTime;
    private Date updateTime;
}