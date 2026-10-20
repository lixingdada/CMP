package mvc.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器。
 *
 * <p>以前每个 DAO 里都写一遍 setAutoCommit(false) / commit / rollback / 恢复自动提交，
 * 而且跨 DAO 的操作合并不到一个事务里。现在事务边界交给调用方声明：
 *
 * <pre>
 * TransactionManager.execute(() -> {
 *     orderDao.insertOrder(order);
 *     cartDao.clearCart(username);
 *     return Boolean.TRUE;
 * });
 * </pre>
 *
 * 回调里的几个 DAO 共用 {@link ConnectionContext} 上的同一条连接，任一步抛异常整体回滚。
 */
public final class TransactionManager {

    private TransactionManager() {
    }

    /** 事务回调，允许抛出 SQLException 由管理器统一回滚 */
    public interface TransactionCallback<T> {
        T doInTransaction() throws SQLException;
    }

    /**
     * 在事务中执行。支持嵌套调用：内层不会开启新事务，也不会提前提交。
     */
    public static <T> T execute(TransactionCallback<T> callback) throws SQLException {
        if (ConnectionContext.inTransaction()) {
            // 已处于事务中，直接执行，事务边界交给最外层
            return callback.doInTransaction();
        }

        Connection connection = ConnectionContext.get();
        boolean borrowed = false;
        if (connection == null) {
            connection = ConnectionPoolFacade.borrow();
            ConnectionContext.bind(connection);
            borrowed = true;
        }

        boolean originAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        ConnectionContext.markTransaction(true);

        try {
            T result = callback.doInTransaction();
            connection.commit();
            return result;
        } catch (SQLException e) {
            safeRollback(connection);
            throw e;
        } catch (RuntimeException e) {
            safeRollback(connection);
            throw e;
        } finally {
            ConnectionContext.markTransaction(false);
            try {
                connection.setAutoCommit(originAutoCommit);
            } catch (SQLException ignored) {
                // 恢复失败不覆盖原始异常
            }
            if (borrowed) {
                ConnectionContext.clear();
            }
        }
    }

    private static void safeRollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // 回滚失败时保留原始异常
        }
    }

    /**
     * 隔离对连接池的直接依赖，避免 persistence 顶层包与 pool 子包循环引用。
     */
    static final class ConnectionPoolFacade {
        private ConnectionPoolFacade() {
        }

        static Connection borrow() throws SQLException {
            return mvc.persistence.pool.ConnectionPool.getInstance().getConnection();
        }
    }
}
