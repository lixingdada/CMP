package com.cmp.mall.common;

import lombok.Getter;

/**
 * 统一响应体。
 *
 * <p>原版 servlet 直接 {@code response.getWriter().write(json)} 拼字符串，每个 Servlet
 * 各写一份格式，前端要按接口分别解析。这里所有接口统一返回 {@code {code, message, data}}，
 * 前端拦截器可以只写一处错误处理。
 */
@Getter
public class Result<T> {

    /** 业务状态码，0 表示成功 */
    private final int code;

    /** 提示信息 */
    private final String message;

    /** 业务数据 */
    private final T data;

    /** 链路追踪号，出现异常时便于在日志里定位 */
    private final String traceId;

    private Result(int code, String message, T data, String traceId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), "ok", data, TraceIdHolder.get());
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null, TraceIdHolder.get());
    }

    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null, TraceIdHolder.get());
    }
}
