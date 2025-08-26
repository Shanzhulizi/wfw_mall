package com.lm.es.dto;

import lombok.Data;

@Data
public class SearchRequestDTO {
    private String keyword;
    private Integer page = 1;
    private Integer size = 20;
    private String sort = "default"; // default, sales, price, new
    private Double minPrice;
    private Double maxPrice;
    private Long categoryId;

    public Integer getFrom() {
        return (page - 1) * size;
    }
}