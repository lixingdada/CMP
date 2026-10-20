package mvc.persistence.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import mvc.persistence.ConnectionContext;

/**
 * 手写数据库连接池。
 *
 * <p>设计要点：
 * <ul>
 *   <li>预创建 + 懒扩容：池满之前按需创建物理连接，之后只复用不再新建</li>
 *   <li>动态代理：把物理连接包装成代理对象返回，调用方执行 close() 时不真正关闭，
 *       而是把连接归还到空闲队列——这样上层代码无需感知"池"的存在，写法与原来完全一致</li>
 *   <li>等待超时：池空时线程在 idle 队列上等待，超时抛异常，避免请求无限挂起</li>
 *   <li>归还前校验：物理连接若已断开（isValid 返回 false）直接丢弃并补建</li>
 *   <li>归还前重置会话状态：回滚未提交事务并恢复 autoCommit，
 *       避免连接被下一个请求复用时带着上一个请求的事务状态</li>
 * </ul>
 *
 * <p>配置可通过 JVM 参数覆盖，例如 -Dcmp.pool.size=20
 * 这样就不再需要把密码硬编码在工具类里。
 */
public final class ConnectionPool {

    private static final String DRIVER = System.getProperty("cmp.jdbc.driver", "com.mysql.cj.jdbc.Driver");
    private static final String URL = System.getProperty("cmp.jdbc.url",
            "jdbc:mysql://127.0.0.1:3306/cmp_2?serverTimezone=Asia/Shanghai&useSSL=false&characterEncoding=utf8");
    private static final String USERNAME = System.getProperty("cmp.jdbc.username", "root");
    private static final String PASSWORD = System.getProperty("cmp.jdbc.password", "123456");

    private static final int MAX_SIZE = Integer.getInteger("cmp.pool.size", 10);
    private static final long BORROW_TIMEOUT_MS = Long.getLong("cmp.pool.timeout", 3000L);

    private static volatile ConnectionPool instance;

    /** 空闲连接队列 */
    private final LinkedList<Connection> idle = new LinkedList<Connection>();

    /** 已创建的物理连接总数（含借出中的） */
    private int createdCount;
    /** 累计借出次数，用于统计 */
    private long borrowCount;
    /** 累计等待次数，用于观察池是否偏小 */
    private long waitCount;

    private ConnectionPool() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL 驱动加载失败：" + DRIVER, e);
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }

    /**
     * 借出一个连接。返回的是动态代理对象，close() 即归还。
     */
    public Connection getConnection() throws SQLException {
        Connection physical = borrowPhysical();
        PooledHandler handler = new PooledHandler(physical);
        Connection proxy = (Connection) Proxy.newProxyInstance(
                ConnectionPool.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                handler);
        handler.bindProxy(proxy);
        return proxy;
    }

    /** 真正从池里取走一个物理连接 */
    private Connection borrowPhysical() throws SQLException {
        long deadline = System.currentTimeMillis() + BORROW_TIMEOUT_MS;
        synchronized (idle) {
            while (true) {
                if (!idle.isEmpty()) {
                    Connection c = idle.removeFirst();
                    if (isHealthy(c)) {
                        borrowCount++;
                        return c;
                    }
                    // 连接已失效，丢弃并允许新建
                    closeQuietly(c);
                    createdCount--;
                    continue;
                }
                if (createdCount < MAX_SIZE) {
                    Connection c = openPhysical();
                    createdCount++;
                    borrowCount++;
                    return c;
                }
                // 池已满且无空闲，等待归还
                long remain = deadline - System.currentTimeMillis();
                if (remain <= 0) {
                    throw new SQLException("获取数据库连接超时（" + BORROW_TIMEOUT_MS + "ms），请检查连接是否泄漏");
                }
                waitCount++;
                try {
                    idle.wait(remain);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("等待数据库连接时被中断", e);
                }
            }
        }
    }

    /** 归还物理连接 */
    private void release(Connection physical) {
        synchronized (idle) {
            if (physical != null && isHealthy(physical)) {
                resetState(physical);
                idle.addLast(physical);
            } else {
                closeQuietly(physical);
                createdCount--;
            }
            idle.notifyAll();
        }
    }

    /**
     * 归还前重置会话状态。
     *
     * <p>直连模式下连接用完即销毁，状态自然消失；一旦池化，连接会被下一个请求复用，
     * 上一次残留的 autoCommit=false 会让后续的单条 SQL 静默不提交。
     * 因此归还原处必须先把未提交的事务回滚掉并恢复自动提交。
     */
    private void resetState(Connection c) {
        try {
            if (!c.getAutoCommit()) {
                c.rollback();
                c.setAutoCommit(true);
            }
        } catch (SQLException ignored) {
            // 重置失败时连接会在下次借出时被 isValid 筛掉
        }
    }

    private Connection openPhysical() throws SQLException {
        Connection c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        c.setAutoCommit(true);
        return c;
    }

    private boolean isHealthy(Connection c) {
        try {
            return c != null && !c.isClosed() && c.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private void closeQuietly(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (SQLException ignored) {
            // 关闭失败无需处理
        }
    }

    /** 池运行状态快照，便于排查连接泄漏 */
    public String stats() {
        synchronized (idle) {
            return "ConnectionPool{created=" + createdCount
                    + ", idle=" + idle.size()
                    + ", inUse=" + (createdCount - idle.size())
                    + ", max=" + MAX_SIZE
                    + ", borrow=" + borrowCount
                    + ", wait=" + waitCount + "}";
        }
    }

    /** 关闭池中所有空闲连接 */
    public void shutdown() {
        synchronized (idle) {
            for (Connection c : idle) {
                closeQuietly(c);
            }
            createdCount -= idle.size();
            idle.clear();
            idle.notifyAll();
        }
    }

    /**
     * 物理连接的代理：拦截 close() 改为归还，其余方法原样转发。
     */
    private final class PooledHandler implements InvocationHandler {

        private final Connection target;
        private Connection proxy;
        private boolean released;

        private PooledHandler(Connection target) {
            this.target = target;
        }

        private void bindProxy(Connection proxy) {
            this.proxy = proxy;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();

            if ("close".equals(name)) {
                if (released) {
                    return null;
                }
                if (ConnectionContext.get() == proxy) {
                    // 请求级连接：所有权在 ConnectionContext，由请求结束时统一归还。
                    // 这里不能回池，否则外层调用还在用这条连接就会被别的请求借走。
                    return null;
                }
                released = true;
                release(target);
                return null;
            }
            if ("isClosed".equals(name)) {
                return Boolean.valueOf(released);
            }

            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                // 把被代理方法抛出的真实异常透传出去，避免被反射包装成 UndeclaredThrowableException
                throw e.getTargetException();
            }
        }
    }
}
