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
 * <p>池满之前按需创建物理连接，之后只复用不新建。借出去的连接用动态代理包了一层，
 * 调用方执行 close() 时把连接还回空闲队列，这样上层代码完全不用改写法。
 * 池空时在 idle 上等，超过 -Dcmp.pool.timeout（默认 3s）抛异常，避免请求挂死。
 * 归还前先重置会话状态，见 {@link #resetState}。
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
     * 归还前把会话状态清干净。
     *
     * <p>这里踩过坑：老代码有不少地方 setAutoCommit(false) 之后没恢复。直连时连接用完就销毁，
     * 没影响；池化之后连接被下一个请求拿去，autoCommit 还是 false，
     * SQL 执行成功但没提交，报错都没有，数据就是没进去。所以还池之前必须 rollback 并恢复成 true。
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
     * 物理连接的代理：只拦 close() 改成归还，其他方法原样转发。
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
                    // 请求级连接，所有权在 ConnectionContext，等请求结束统一还。
                    // 这里回池的话，外层还在用的连接会被别的请求借走。
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
