package mvc.persistence;

import java.sql.Connection;

/**
 * 线程级连接上下文。
 *
 * <p>一次请求由容器分一条线程处理，把 Connection 绑到 ThreadLocal 上，
 * 同一个请求里几个 DAO 就能共用一条连接，事务也才能按请求来划边界。
 * 线程是复用的，请求结束必须 clear，不然下一个请求会拿到残留的连接或事务标记。
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
        // 先解绑再关闭：代理的 close() 会判断"是不是当前线程绑定的连接"，
        // 若不先解绑就会被当成请求级连接而拒绝归还。
        HOLDER.remove();
        IN_TX.remove();
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
                // 归还失败不影响请求结束
            }
        }
    }
}
