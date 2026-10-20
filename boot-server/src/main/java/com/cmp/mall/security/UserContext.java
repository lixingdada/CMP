package com.cmp.mall.security;

/**
 * 当前请求的登录用户，基于 ThreadLocal。
 *
 * <p>与手写版里的 {@code ConnectionContext} 是同一个思路：把"一次请求内共享的状态"
 * 绑到处理该请求的线程上，用完必须清理。区别是这里存的是身份而不是数据库连接。
 * Tomcat 线程会复用，忘记 remove 会导致下一个请求读到上一个用户的身份——这正是
 * ThreadLocal 最典型的坑。
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
