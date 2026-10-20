package mvc.persistence;

import mvc.persistence.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * JDBC 工具类。
 *
 * <p>连接不再放在静态字段里（初版那样并发会互相覆盖），改成绑到当前线程。
 * 一个请求一条连接，从 {@link ConnectionPool} 借，请求结束时由
 * {@code ConnectionReleaseListener} 统一归还。
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
     *
     * <p>当前线程绑定的那条不能在这里还池。一个请求内多个 DAO 共用一条连接，
     * 内层 DAO 先 close 的话，外层还在用的连接就被别的请求借走了。
     * 归还统一放在请求结束的时候。
     */
    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        if (connection == ConnectionContext.get()) {
            return;
        }
        if (ConnectionContext.inTransaction()) {
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

    /**
     * 兼容旧代码保留的空方法。
     *
     * <p>DAO 里几十处 {@code DBUtil.close()} 都还在调，保留是为了不改那些调用点。
     */
    public static void close() {
        // 请求结束统一归还，这里什么都不做
    }

    /**
     * 归还当前线程绑定的连接。由 ConnectionReleaseListener 在请求结束时调。
     * 没有 Web 容器的场景（测试、命令行）得自己调一下。
     */
    public static void releaseThreadConnection() {
        ConnectionContext.clear();
    }

    /** 连接池运行状态，便于排查连接泄漏 */
    public static String stats() {
        return ConnectionPool.getInstance().stats();
    }
}
