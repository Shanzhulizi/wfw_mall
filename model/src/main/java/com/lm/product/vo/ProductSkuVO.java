package com.lm.product.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductSkuVO {
    private Long id;
    private Long spuId;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String attrValueJson;
    private Date createTime;
    private Date updateTime;
}