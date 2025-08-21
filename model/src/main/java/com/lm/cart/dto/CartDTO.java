package com.lm.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartDTO {

    String skuName;
    BigDecimal price;
    String image;

}
