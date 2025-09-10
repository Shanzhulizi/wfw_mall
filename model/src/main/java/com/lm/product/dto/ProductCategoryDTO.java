package com.lm.product.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductCategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer status;
    private Integer sort;
    private Date createTime;
    private Date updateTime;

    // 可选：父级分类信息（用于构建分类路径）
    private ProductCategoryDTO parentCategory;

    // 可选：子分类列表
    private List<ProductCategoryDTO> children;
}