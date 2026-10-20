package com.cmp.mall.mapper;

import com.cmp.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    /** 按分类查询；keyword 为空时不参与过滤，由 XML 里的 if 动态拼接 */
    List<Product> findByCategory(@Param("categoryId") String categoryId,
                                 @Param("keyword") String keyword);

    Product findById(@Param("productId") String productId);

    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
