package com.cmp.mall.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志埋点。
 *
 * <p>对照原版：原版在 web.xml 里把 Filter 挂到下单与支付入口，
 * 只能覆盖 URL 级别，日志内容还得在 Filter 里重新把购物车查一遍。
 * 换成注解 + 切面后，想在哪个业务动作上打日志就在那个方法上加一行，
 * 日志内容直接来自方法参数，不再需要反查。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 动作类型，写入 user_log.action，例如 ADD_CART / CREATE_ORDER */
    String action();

    /** 是否记录方法参数摘要 */
    boolean withArgs() default true;
}
