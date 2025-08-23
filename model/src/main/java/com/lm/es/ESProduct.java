package com.lm.es;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ESProduct {
    private Long id;
    private String name;
    private Long categoryId;
    private Long brandId;
    private Long merchantId;
    private String description;
    private String mainImage;

    private Double minPrice;
    private Double maxPrice;
    private Integer totalStock;

    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommended;
    private Integer saleCount;
}
