package com.lm.cart.dto;

import lombok.Data;

@Data
public class AddCartDTO {
    private Long skuId;
    private Integer count;
}
