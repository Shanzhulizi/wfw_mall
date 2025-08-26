package com.lm.product.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
// ProductBrandDTO.java
@Data
public class ProductBrandDTO {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private Integer sort;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}