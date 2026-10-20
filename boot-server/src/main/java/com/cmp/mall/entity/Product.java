package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;

/** 商品（一个商品下挂多个条目 / SKU） */
@Data
public class Product implements Serializable {
    private String productId;
    private String category;
    private String name;
    private String descn;

    /**
     * 该商品下所有 SKU 的最低价，非表字段。
     * 列表页展示"¥16.50 起"用，由 SQL 聚合得出，避免前端为每个商品再请求一次 SKU 列表。
     */
    private java.math.BigDecimal minPrice;
}
