package com.lm.cart.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItem {

    private Long skuId;//购物车一般只要一个具体的，不需要大类spu
    private String skuName;//商品名
    private String image;
    private BigDecimal price;
    private Integer count;
    private Boolean checked; // 是否勾选结算


    // fromString：将 JSON 字符串转为 CartItem 对象
    public static CartItem fromString(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, CartItem.class);
        } catch (Exception e) {
            throw new RuntimeException("反序列化 CartItem 出错", e);
        }
    }

    // toString：将对象转为 JSON 字符串（建议重写）
    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("序列化 CartItem 出错", e);
        }
    }

}