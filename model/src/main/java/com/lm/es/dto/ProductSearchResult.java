package com.lm.es.dto;


import lombok.Data;

import java.util.List;
import java.util.Map;

//@Data
//public class ProductSearchResult {
//    private List<ProductSearchItem> products;
//    private Long total;
//    private Integer page;
//    private Integer size;
//    private Integer totalPages;
//
//    public ProductSearchResult(List<ProductSearchItem> products, Long total, Integer page, Integer size) {
//        this.products = products;
//        this.total = total;
//        this.page = page;
//        this.size = size;
//        this.totalPages = (int) Math.ceil((double) total / size);
//    }
//}


@Data
public class ProductSearchResult {
    private List<ProductSearchItem> products;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Map<String, Object> aggregations; // 聚合结果（可选，用于分类统计等）

    public ProductSearchResult(List<ProductSearchItem> products, Long total, Integer page, Integer size) {
        this.products = products;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    public ProductSearchResult(List<ProductSearchItem> products, Long total, Integer page, Integer size,
                               Map<String, Object> aggregations) {
        this(products, total, page, size);
        this.aggregations = aggregations;
    }
}