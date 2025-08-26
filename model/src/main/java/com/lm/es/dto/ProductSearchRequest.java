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
    private String keyword;          // 关键词
    private Long categoryId;         // 分类ID
    private Long brandId;            // 品牌ID
    private Long merchantId;         // 商家ID
    private Integer minPrice;        // 最低价格
    private Integer maxPrice;        // 最高价格
    private Integer status;          // 状态
    private Integer isHot;           // 是否热销
    private Integer isNew;           // 是否新品
    private Integer isRecommended;   // 是否推荐

    // 排序字段选项扩展
    private String sortField = "saleCount"; // 可选：saleCount, price, createTime, _score
    private String sortOrder = "desc";      // 默认降序

    // 分页参数
    private Integer page = 1;
    private Integer size = 20;

    public Integer getFrom() {
        return (page - 1) * size;
    }
}