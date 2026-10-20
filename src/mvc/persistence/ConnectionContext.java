package mvc.persistence;

import java.sql.Connection;

/**
 * 线程级连接上下文。
 *
 * <p>一个 HTTP 请求由容器分配一条工作线程处理，因此把 Connection 绑定到 ThreadLocal 上，
 * 可以保证同一次请求内多个 DAO 复用同一个物理连接——既减少连接创建开销，
 * 也让"一次请求一个事务"成为可能：事务的边界由请求线程决定，而不是由某个 DAO 决定。
 *
 * <p>线程复用是容器线程池的固有特性，所以请求结束时必须调用 {@link #clear()}，
 * 否则下一个请求可能拿到上一个请求残留的连接或事务状态。
 */
public final class ConnectionContext {

    private static final ThreadLocal<Connection> HOLDER = new ThreadLocal<Connection>();
    private static final ThreadLocal<Boolean> IN_TX = new ThreadLocal<Boolean>();

    private ConnectionContext() {
    }

    public static Connection get() {
        return HOLDER.get();
    }

    public static void bind(Connection connection) {
        HOLDER.set(connection);
    }

    public static boolean inTransaction() {
        return Boolean.TRUE.equals(IN_TX.get());
    }

    static void markTransaction(boolean flag) {
        if (flag) {
            IN_TX.set(Boolean.TRUE);
        } else {
            IN_TX.remove();
        }
    }

    /** 解绑并归还连接（若存在） */
    public static void clear() {
        Connection c = HOLDER.get();
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
                // 归还失败不影响请求结束
            }
        }
        HOLDER.remove();
        IN_TX.remove();
    }
}
