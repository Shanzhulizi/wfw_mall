package com.lm.product.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSku {
    Long id;
    Long spuId; // 商品spu的id
    String skuName; // 商品sku的名称
    BigDecimal price;
    Integer stock; // 商品sku的库存
    String image; // 商品sku的图片
    String attrValueJson;
    LocalDateTime createTime; // 创建时间
    LocalDateTime updateTime; // 更新时间
}
