package com.lm.product.domain;

import lombok.Data;

import java.util.List;

@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer status;
    private Integer sort;

//    @TableField(exist = false)
    private List<Category> children;

}
