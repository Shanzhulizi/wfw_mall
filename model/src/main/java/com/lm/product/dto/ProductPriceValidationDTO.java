package com.lm.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductPriceValidationDTO {
    Long skuId;
    BigDecimal price;
    Long spuId;
}
