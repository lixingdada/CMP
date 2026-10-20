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
 *   <li><b>连接按请求复用</b>：同一个请求内多次 getConnection() 返回同一条连接，
 *       DAO 里原有的 close 调用不再真正断开，统一由请求结束时的
 *       {@code ConnectionReleaseListener} 归还，既消除连接泄漏，也避免内层 DAO
 *       提前把外层还在用的连接放回池中。</li>
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
     *
     * <p>连接的所有权在线程上下文 {@link ConnectionContext}：同一个请求内所有 DAO 共享同一条连接，
     * 因此这里对"当前线程绑定的那条连接"不做归还——否则内层 DAO 一关闭，外层 DAO 正在用的连接
     * 就回到了池里，可能被别的请求借走。真正的归还统一发生在请求结束时。
     *
     * <p>只有非线程绑定的连接（理论上不会出现）才真正 close。
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
     * 兼容旧代码保留的空操作。
     *
     * <p>改造后连接是请求级的：同一个请求复用一条连接，直到请求结束才统一归还。
     * 所以 DAO 内部的 close 不再代表"断开连接"，只是历史写法，这里保留是为了不改动 60 多处调用点。
     */
    public static void close() {
        // 由请求结束时的 releaseThreadConnection() 统一归还，此处不做处理
    }

    /**
     * 归还当前线程绑定的连接。
     *
     * <p>由 {@code ConnectionReleaseListener} 在请求结束时调用；没有 Web 容器的场景
     * （例如单元测试、命令行任务）需要手动调用。
     */
    public static void releaseThreadConnection() {
        ConnectionContext.clear();
    }

    /** 连接池运行状态，便于排查连接泄漏 */
    public static String stats() {
        return ConnectionPool.getInstance().stats();
    }
}
