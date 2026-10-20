package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/** 购物车条目 */
@Data
public class CartItem implements Serializable {
    private Integer cartId;
    private String itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private Integer quantity;
}
