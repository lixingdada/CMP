package com.cmp.mall.dto;

import com.cmp.mall.entity.Item;
import com.cmp.mall.entity.Product;

import java.util.List;

/** 商品详情：商品本身 + 挂在其下的所有 SKU */
public record ProductDetailView(
        Product product,
        List<Item> items
) {
}
