package com.cmp.mall.dto;

import java.time.LocalDate;

/** 个人中心视图：account + receiver 两张表合成一个对象给前端 */
public record ProfileView(
        String userId,
        String virtualName,
        LocalDate birthday,
        String email,
        String phone,
        String avatar,
        String receiverName,
        String receiverPhone,
        String receiverAddr
) {
}
