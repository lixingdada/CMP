package com.cmp.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细行。price 是下单时刻的价格快照——商品后续调价不影响历史订单。
 * 用普通类接收 JOIN 结果，itemName 由 item + product 关联出来。
 */
@Data
public class OrderItemView {
    private String itemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
