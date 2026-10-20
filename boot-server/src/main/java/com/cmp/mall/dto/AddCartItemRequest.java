package com.cmp.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotBlank(message = "商品条目不能为空") String itemId,
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为 1")
        Integer quantity
) {
}
