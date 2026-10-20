package com.cmp.mall.common;

import lombok.Getter;

/**
 * 业务错误码。
 *
 * <p>原版是直接抛 {@link RuntimeException} 然后 Servlet 里 catch 住返回一句中文，
 * 前端无法区分"未登录"和"库存不足"。这里把错误分类成可判定的码。
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "ok"),

    /* ---------- 客户端错误 1xxx ---------- */
    PARAM_INVALID(1001, "参数不合法"),
    UNAUTHORIZED(1002, "未登录或登录已过期"),
    FORBIDDEN(1003, "无权访问该资源"),

    /* ---------- 业务错误 2xxx ---------- */
    USERNAME_EXISTS(2001, "用户名已被占用"),
    LOGIN_FAILED(2002, "用户名或密码错误"),
    CATEGORY_NOT_FOUND(2003, "分类不存在"),
    PRODUCT_NOT_FOUND(2004, "商品不存在"),
    ITEM_NOT_FOUND(2005, "商品条目不存在"),
    OUT_OF_STOCK(2006, "库存不足"),
    CART_EMPTY(2007, "购物车为空，无法下单"),
    ORDER_NOT_FOUND(2008, "订单不存在"),

    /* ---------- 服务端错误 5xxx ---------- */
    INTERNAL_ERROR(5000, "服务器内部错误"),
    DB_ERROR(5001, "数据库操作失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
