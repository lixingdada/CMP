package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;

/** 购物车（一个用户一条） */
@Data
public class Cart implements Serializable {
    private Integer cartId;
    private String username;
}
