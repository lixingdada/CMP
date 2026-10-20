package com.cmp.mall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 资料更新请求。全部字段可空，为 null 表示不修改——
 * 对应 XML 里 updateAccount / updateReceiver 的 {@code <if>} 动态 SET。
 */
public record UpdateProfileRequest(
        @Size(max = 20, message = "昵称最长 20 个字符") String virtualName,
        LocalDate birthday,
        @Email(message = "邮箱格式不正确") String email,
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
        String avatar,
        @Size(max = 50, message = "收货人姓名过长") String receiverName,
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "收货电话格式不正确") String receiverPhone,
        @Size(max = 255, message = "地址过长") String receiverAddr
) {
}
