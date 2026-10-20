package com.cmp.mall.security;

/**
 * 当前请求的登录用户，放在 ThreadLocal 里。
 *
 * <p>Tomcat 线程是复用的，用完不 remove 的话下一个请求会读到上一个用户的身份。
 */
public final class UserContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(String username) {
        CURRENT.set(username);
    }

    /** 未登录时返回 null */
    public static String get() {
        return CURRENT.get();
    }

    /** 取当前用户，缺失时抛未登录异常 */
    public static String require() {
        String username = CURRENT.get();
        if (username == null) {
            throw new com.cmp.mall.common.BizException(com.cmp.mall.common.ErrorCode.UNAUTHORIZED);
        }
        return username;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
