package com.lm.product.domain;

import lombok.Data;

@Data
public class Brand {
    private Long id;
    private String name;
    private String logo;
    private String description;
}