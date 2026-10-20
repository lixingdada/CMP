package mvc.persistence;

import mvc.persistence.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * JDBC 工具类（连接池版）。
 *
 * <p>相对初版的三处修复：
 * <ol>
 *   <li><b>连接不再共享静态字段</b>：初版把 Connection / PreparedStatement / ResultSet 声明为 static，
 *       多线程并发时互相覆盖，是典型的线程安全缺陷。现在连接绑定在线程上下文
 *       {@link ConnectionContext} 上，一个请求一条连接，互不干扰。</li>
 *   <li><b>引入连接池</b>：初版每次 getConnection() 都新建物理连接，开销大且并发一高就崩。
 *       现在从 {@link ConnectionPool} 借出，close() 归还而不是真正断开。</li>
 *   <li><b>密码不再硬编码</b>：改为从 JVM 参数读取，默认值仅用于本地开发。</li>
 * </ol>
 *
 * <p>对外方法签名与初版保持完全一致，因此原有的 60 多个调用点无需修改即可受益。
 */
public class DBUtil {

    private DBUtil() {
    }

    /**
     * 获取当前线程的连接。同一次请求内多次调用返回同一个连接。
     */
    public static Connection getConnection() {
        Connection bound = ConnectionContext.get();
        if (bound != null) {
            try {
                if (!bound.isClosed()) {
                    return bound;
                }
            } catch (SQLException ignored) {
                // 落到下面重新借
            }
            ConnectionContext.clear();
        }
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            ConnectionContext.bind(connection);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接失败", e);
        }
    }

    /** 获取预编译命令对象 */
    public static PreparedStatement getpreparedStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("SQL 预编译失败：" + sql, e);
        }
    }

    /** 带参数的改 / 删 / 增，返回影响行数 */
    public static int executeUpdate(String sql, ArrayList<Object> params) {
        PreparedStatement ps = null;
        try {
            ps = getpreparedStatement(sql);
            bindParams(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("执行更新失败：" + sql, e);
        } finally {
            closePreparedStatement(ps);
        }
    }

    /** 带参数的查询。调用方负责关闭 ResultSet。 */
    public static ResultSet executeQuery(String sql, ArrayList<Object> params) {
        try {
            PreparedStatement ps = getpreparedStatement(sql);
            bindParams(ps, params);
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("执行查询失败：" + sql, e);
        }
    }

    private static void bindParams(PreparedStatement ps, ArrayList<Object> params) throws SQLException {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    /**
     * 关闭连接。
     * <p>事务进行中时不做处理——连接的所有权在 {@link TransactionManager}，由它负责提交或回滚。
     * 否则把连接归还连接池。
     */
    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        if (ConnectionContext.inTransaction()) {
            return;
        }
        if (connection == ConnectionContext.get()) {
            ConnectionContext.clear();
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement == null) {
            return;
        }
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 结束当前线程的数据库资源，归还连接池 */
    public static void close() {
        ConnectionContext.clear();
    }

    /** 连接池运行状态，便于排查连接泄漏 */
    public static String stats() {
        return ConnectionPool.getInstance().stats();
    }
}
