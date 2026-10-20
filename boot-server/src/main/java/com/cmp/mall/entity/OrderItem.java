package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/** 订单明细。price 为下单时刻的价格快照，避免商品调价影响历史订单 */
@Data
public class OrderItem implements Serializable {
    private Integer orderId;
    private String itemId;
    private Integer quantity;
    private BigDecimal price;
    private String username;
}
