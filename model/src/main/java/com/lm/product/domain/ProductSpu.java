package com.lm.product.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ProductSpu {
    Long id;
    String name;
    Long categoryId;
    Long brandId;
    Long merchantId; // 商户id
    String description; // 商品描述
    String mainImage;
    Integer status; // 商品状态 0:下架 1:上架
    Integer isHot;
    Integer isNew;
    Integer isRecommended; // 是否推荐
    Integer saleCount;  //销量
    Date createTime; // 创建时间
   Date  updateTime; // 更新时间

}
