package com.cmp.mall.common;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * 请求级 traceId 的持有者。
 *
 * <p>基于 SLF4J 的 MDC 实现：拦截器在请求进入时生成一个短 id 放进 MDC，
 * logback 的 pattern 里直接引用 {@code %X{traceId}}，于是同一请求的所有日志
 * 都能串起来。响应体里也带一份，用户报错时把 traceId 发过来就能直接检索。
 */
public final class TraceIdHolder {

    public static final String KEY = "traceId";

    private TraceIdHolder() {
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static void set(String traceId) {
        MDC.put(KEY, traceId);
    }

    public static String get() {
        String traceId = MDC.get(KEY);
        return traceId == null ? "" : traceId;
    }

    public static void clear() {
        MDC.remove(KEY);
    }
}
