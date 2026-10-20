import mvc.domain.Category;
import mvc.persistence.ConnectionContext;
import mvc.persistence.DBUtil;
import mvc.persistence.Impl.CategoryDaoImpl;
import mvc.persistence.TransactionManager;
import mvc.persistence.pool.ConnectionPool;

import java.sql.Connection;
import java.util.List;

/** 连接池 / 线程上下文 / 事务模板 / 缓存的单元级验证 */
public class PoolContextTest {

    static int pass = 0, fail = 0;

    static void check(String name, boolean cond) {
        if (cond) { pass++; System.out.println("  PASS  " + name); }
        else { fail++; System.out.println("  FAIL  " + name); }
    }

    public static void main(String[] args) throws Exception {
        ConnectionPool pool = ConnectionPool.getInstance();

        // ---------- 1. 借还基本行为 ----------
        Connection c1 = DBUtil.getConnection();
        check("1.1 借出连接非空且可用", c1 != null && !c1.isClosed() && c1.isValid(2));
        check("1.2 借出后连接被线程上下文持有", ConnectionContext.get() == c1);

        int idleBefore = idleOf(pool.stats());
        c1.close(); // 请求级连接：此时应被忽略，不回池
        check("1.3 请求级连接调用 close() 不提前回池", idleOf(pool.stats()) == idleBefore);
        check("1.4 关闭后仍可继续使用", c1.createStatement().executeQuery("SELECT 1").next());

        // ---------- 2. 同一请求内复用同一连接 ----------
        Connection c2 = DBUtil.getConnection();
        check("2.1 同线程多次取连接为同一对象", c1 == c2);

        // ---------- 3. 请求结束统一归还 ----------
        idleBefore = idleOf(pool.stats());
        DBUtil.releaseThreadConnection();
        check("3.1 请求结束后连接回池", idleOf(pool.stats()) == idleBefore + 1);
        check("3.2 上下文已解绑", ConnectionContext.get() == null);
        check("3.3 归还后 autoCommit 已重置", borrowedAutoCommit(pool));

        // ---------- 4. 分类查询 + 缓存 ----------
        CategoryDaoImpl dao = new CategoryDaoImpl();
        List<Category> list = dao.getCategoryList();
        check("4.1 分类查询返回 5 条", list.size() == 5);
        String firstId = list.get(0).getCategoryId();
        check("4.2 按 ID 查分类命中", dao.getCategory(firstId) != null);

        String statBefore = pool.stats();
        dao.getCategoryList();
        dao.getCategory(firstId);
        check("4.3 缓存命中后不再借连接", statBefore.equals(pool.stats()));

        // ---------- 5. 事务模板：异常回滚 ----------
        DBUtil.releaseThreadConnection();
        boolean thrown = false;
        try {
            TransactionManager.execute(new TransactionManager.TransactionCallback<Object>() {
                public Object doInTransaction() throws java.sql.SQLException {
                    Connection conn = DBUtil.getConnection();
                    conn.createStatement().executeUpdate(
                            "UPDATE category SET name = 'ROLLBACK_TEST' WHERE catid = (SELECT x.c FROM (SELECT MIN(catid) c FROM category) x)");
                    throw new RuntimeException("intentional");
                }
            });
        } catch (RuntimeException e) {
            thrown = true;
        }
        check("5.1 事务内异常向外抛出", thrown);
        DBUtil.releaseThreadConnection();
        Connection v = DBUtil.getConnection();
        java.sql.ResultSet rs = v.createStatement().executeQuery(
                "SELECT COUNT(*) FROM category WHERE name = 'ROLLBACK_TEST'");
        rs.next();
        check("5.2 异常后整体回滚", rs.getInt(1) == 0);
        rs.close();
        DBUtil.releaseThreadConnection();

        // ---------- 6. 事务内复用连接、嵌套不重复开启事务 ----------
        DBUtil.releaseThreadConnection();
        Boolean same = TransactionManager.execute(new TransactionManager.TransactionCallback<Boolean>() {
            public Boolean doInTransaction() throws java.sql.SQLException {
                Connection x = DBUtil.getConnection();
                Connection y = DBUtil.getConnection();
                boolean nested = TransactionManager.execute(new TransactionManager.TransactionCallback<Boolean>() {
                    public Boolean doInTransaction() {
                        return Boolean.valueOf(ConnectionContext.inTransaction());
                    }
                }).booleanValue();
                return Boolean.valueOf((x == y) && nested);
            }
        });
        check("6.1 事务内共用同一连接且嵌套不重开事务", same.booleanValue());

        // ---------- 7. 池耗尽按超时失败 ----------
        DBUtil.releaseThreadConnection();
        Connection[] held = new Connection[10];
        for (int i = 0; i < held.length; i++) {
            held[i] = ConnectionPool.getInstance().getConnection();
        }
        long t0 = System.currentTimeMillis();
        boolean timeout = false;
        try {
            ConnectionPool.getInstance().getConnection();
        } catch (java.sql.SQLException e) {
            timeout = true;
        }
        long cost = System.currentTimeMillis() - t0;
        check("7.1 池满后借连接抛超时异常", timeout);
        check("7.2 等待约 3 秒（实测 " + cost + "ms）", cost >= 2500 && cost <= 6000);
        for (Connection h : held) {
            h.close();
        }
        check("7.3 归还后可再次借出", ConnectionPool.getInstance().getConnection().isValid(2));
        DBUtil.releaseThreadConnection();

        System.out.println("\n池状态: " + pool.stats());
        System.out.println("总计: " + pass + " 通过, " + fail + " 失败");
        pool.shutdown();
        if (fail > 0) System.exit(1);
    }

    static int idleOf(String stats) {
        int i = stats.indexOf("idle=");
        int j = stats.indexOf(',', i);
        return Integer.parseInt(stats.substring(i + 5, j).trim());
    }

    static boolean borrowedAutoCommit(ConnectionPool pool) throws Exception {
        DBUtil.releaseThreadConnection();
        Connection c = DBUtil.getConnection();
        boolean ac = c.getAutoCommit();
        DBUtil.releaseThreadConnection();
        return ac;
    }
}
