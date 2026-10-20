package com.cmp.mall.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartView(
        Integer cartId,
        List<CartLineView> items,
        int totalQuantity,
        BigDecimal totalAmount
) {
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
