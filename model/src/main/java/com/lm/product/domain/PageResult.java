package com.lm.product.domain;

import lombok.Data;

import java.util.List;

// 通用的分页结果类
@Data
public class PageResult<T> {
    private Long total;
    private Integer page;
    private Integer size;
    private List<T> list;

    public PageResult() {}

    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public PageResult(Long total, Integer page, Integer size, List<T> list) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.list = list;
    }
}