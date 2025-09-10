package com.lm.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.lm.product.domain.Category;
import com.lm.product.mapper.CategoryMapper;
import com.lm.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CATEGORY_KEY = "category:tree";

    @Override
    public List<Category> getCategoryTree() {
        // 1. 先查缓存
        String cache = redisTemplate.opsForValue().get(CATEGORY_KEY);
        if (cache != null) {
            return JSON.parseArray(cache, Category.class);
        }

        // 2. 查数据库
        List<Category> all = categoryMapper.selectAll();

        // 3. 构建树
        List<Category> tree = buildTree(all);

        // 4. 存入缓存，1小时过期
        redisTemplate.opsForValue().set(CATEGORY_KEY, JSON.toJSONString(tree), 1, TimeUnit.HOURS);

        return tree;
    }

    private List<Category> buildTree(List<Category> all) {
        return all.stream()
                .filter(c -> c.getParentId() == 0) // 一级分类
                .map(c -> {
                    c.setChildren(getChildren(c, all));
                    return c;
                })
                .collect(Collectors.toList());
    }

    private List<Category> getChildren(Category root, List<Category> all) {
        return all.stream()
                .filter(c -> root.getId().equals(c.getParentId()))
                .map(c -> {
                    c.setChildren(getChildren(c, all));
                    return c;
                })
                .collect(Collectors.toList());
    }
}
