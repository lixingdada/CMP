package com.cmp.mall.mapper;

import com.cmp.mall.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> findAll();

    Category findById(@Param("catId") String catId);

    int countById(@Param("catId") String catId);
}
