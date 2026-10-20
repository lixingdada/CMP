package com.cmp.mall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderView(
        Integer orderId,
        String orderName,
        String orderAddress,
        String orderTel,
        LocalDateTime orderDate,
        List<OrderItemView> items,
        BigDecimal totalAmount
) {
}
