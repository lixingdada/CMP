package com.cmp.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "收货人不能为空")
        @Size(max = 50, message = "收货人姓名过长")
        String orderName,

        @NotBlank(message = "收货地址不能为空")
        @Size(max = 255, message = "地址过长")
        String orderAddress,

        @NotBlank(message = "联系电话不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String orderTel
) {
}
