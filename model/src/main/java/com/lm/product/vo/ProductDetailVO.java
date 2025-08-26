package com.lm.product.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Long merchantId;
    private String shopName;
    private String description;
    private String mainImage;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommended;
    private Integer saleCount;
    private Date createTime;
    private Date updateTime;

    // SKU信息
    private List<ProductSkuVO> skus;
}