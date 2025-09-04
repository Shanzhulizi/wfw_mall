package com.lm.es.dto;

import lombok.Data;

import java.math.BigDecimal;
//@Data
//public class ProductSearchRequest {
//    private String keyword;          // 关键词
//    private Long categoryId;         // 分类ID
//    private Long brandId;            // 品牌ID
//    private Long merchantId;         // 商家ID
//    private Integer minPrice;        // 最低价格
//    private Integer maxPrice;        // 最高价格
//    private Integer status;          // 状态
//    private Integer isHot;           // 是否热销
//    private Integer isNew;           // 是否新品
//    private Integer isRecommended;   // 是否推荐
//
//    // 排序字段
//    private String sortField = "saleCount"; // 默认按销量排序
//    private String sortOrder = "desc";      // 默认降序
//
//    // 分页参数
//    private Integer page = 1;
//    private Integer size = 20;
//
//    public Integer getFrom() {
//        return (page - 1) * size;
//    }
//
//
//}


@Data
public class ProductSearchRequest {
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sort; // 接收前端的排序标识
    private Integer page = 1;
    private Integer size = 20;

    // 计算from参数
    public int getFrom() {
        return (page - 1) * size;
    }

    public Integer getSize() {
        return size != null ? size : 20;
    }
}