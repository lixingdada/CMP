package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;

/** 商品分类 */
@Data
public class Category implements Serializable {
    private String catId;
    private String name;
    private String descn;
}
