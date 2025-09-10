package com.lm.order.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private String orderNo;
    private Long spuId;
    private Long skuId;//一个spu可有多个sku，但是一个订单项只能对应一个sku
    private String productName;
    private String skuAttrs;   // 如：颜色:红, 尺码:XL
    private BigDecimal price;   // 单价
    private Integer quantity;   //  购买数量
    private String image;
    private BigDecimal totalPrice;  // 小计 = 单价 * 数量
}
