package mvc.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器：用模板方法把事务边界从业务代码里收敛出来。
 *
 * <p>改造前，每个 DAO 内部都要重复写一遍
 * {@code setAutoCommit(false) → 执行 → commit() → catch → rollback() → finally → 恢复自动提交}，
 * 而且多个 DAO 之间的操作无法合并到同一个事务里。
 *
 * <p>改造后，事务边界由调用方声明：
 * <pre>
 * TransactionManager.execute(new TransactionCallback&lt;Boolean&gt;() {
 *     public Boolean doInTransaction() throws SQLException {
 *         orderDao.insertOrder(order);
 *         cartDao.clearCart(username);
 *         return Boolean.TRUE;
 *     }
 * });
 * </pre>
 * 两个 DAO 共享 {@link ConnectionContext} 中的同一个连接，任一步失败整体回滚。
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
