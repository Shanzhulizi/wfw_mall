package com.lm.product.mapper;

import com.lm.product.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectAll();


    @Select("SELECT * FROM product_category WHERE id = #{categoryId}")
    Category selectById(Long categoryId);
}
