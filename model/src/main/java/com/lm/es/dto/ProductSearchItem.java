package com.lm.es.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//@Data
//public class ProductSearchItem {
//    private Long id;
//    private String name;
//    private String description;
//    private String mainImage;
//    private BigDecimal minPrice;     // 最低价格（从SKU中取）
//    private BigDecimal maxPrice;     // 最高价格（从SKU中取）
//    private Integer saleCount;
//    private String categoryName;
//    private String brandName;
//    private String shopName;
//    private Integer status;
//    private Integer isHot;
//    private Integer isNew;
//    private Integer isRecommended;
//
//
//}

@Data
public class ProductSearchItem {
    private Long id;                    // 商品ID
    private String name;                // 商品名称
    private Long categoryId;            // 分类ID
    private Long brandId;               // 品牌ID
    private Long merchantId;            // 商家ID
    private String description;         // 商品描述
    private String mainImage;           // 主图
    private Integer status;             // 状态
    private Integer isHot;              // 是否热销
    private Integer isNew;              // 是否新品
    private Integer isRecommended;      // 是否推荐
    private Integer saleCount;          // 销量
    private Date createTime;            // 创建时间
    private Date updateTime;            // 更新时间

    // 关联信息
    private String categoryName;        // 分类名称
    private String brandName;           // 品牌名称
    private String shopName;            // 店铺名称

    // 价格范围（从SKU计算）
    private BigDecimal minPrice;        // 最低价格
    private BigDecimal maxPrice;        // 最高价格

    // SKU列表
    private List<SkuItem> skus;

    // 高亮字段
    private String highlightedName;
    private String highlightedDescription;

    @Data
    public static class SkuItem {
        private String id;              // SKU ID
        private String skuName;         // SKU名称
        private BigDecimal price;       // 价格
        private Integer stock;          // 库存
        private String image;           // 图片
        private String attrValueJson;   // 属性值JSON
    }
}

