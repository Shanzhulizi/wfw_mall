package com.lm.product.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ProductSku {
    Long id;
    Long spuId; // 商品spu的id
    String skuName; // 商品sku的名称
    BigDecimal price;
    Integer stock; // 商品sku的库存
    String image; // 商品sku的图片
    String attrValueJson;
    Date createTime; // 创建时间
    Date updateTime; // 更新时间
}
