package com.cmp.mall.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志埋点。
 *
 * <p>加在需要记日志的方法上，切面负责取参数、写 user_log。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 动作类型，写入 user_log.action，例如 ADD_CART / CREATE_ORDER */
    String action();

    /** 是否记录方法参数摘要 */
    boolean withArgs() default true;
}
