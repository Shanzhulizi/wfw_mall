package com.lm.product.controller;

import com.lm.common.R;
import com.lm.product.domain.Category;
import com.lm.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public R list() {
        // 获取分类树
        List<Category> tree = categoryService.getCategoryTree();
        // 返回 R.ok(描述, 数据)
        return R.ok("获取分类成功", tree);
    }
}

