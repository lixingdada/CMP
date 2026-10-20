package com.cmp.mall.common;

import lombok.Getter;

/**
 * 业务异常。
 *
 * <p>继承 {@link RuntimeException} 而不是受检异常，避免 Service 层方法签名被 throws 污染；
 * 由 {@link GlobalExceptionHandler} 统一捕获并翻译成 {@link Result}。
 */
@Getter
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
