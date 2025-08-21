package com.lm.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPriceValidationDTO {
    Long skuId;
    BigDecimal price;
    Long spuId;
}
