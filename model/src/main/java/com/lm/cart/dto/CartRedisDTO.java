package com.lm.cart.dto;

import lombok.Data;

@Data
public class CartRedisDTO {
    private Long skuId; // 商品ID
    private String name; // 商品名称
    private String image; // 商品图片
    private String price; // 商品价格
    private Integer count; // 商品数量
    private Boolean checked; // 是否勾选结算


}
