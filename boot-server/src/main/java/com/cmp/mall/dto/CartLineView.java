package com.cmp.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车单行。用普通类而不是 record，是为了让 MyBatis 能直接把一条
 * cart_item LEFT JOIN item 的结果映射进来（一次查完，避免按行回查库存造成 N+1）。
 */
@Data
public class CartLineView {
    private String itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private Integer quantity;
    /** 实时库存 */
    private Integer stock;
    /** 小计，由服务端计算，避免前端浮点误差 */
    private BigDecimal subtotal;
}
