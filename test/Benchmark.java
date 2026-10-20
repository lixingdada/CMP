import mvc.persistence.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 连接池与缓存的并发基准测试。
 *
 * <p><b>测什么</b>
 * <ul>
 *   <li>连接池：对比「每次 DriverManager 新建物理连接」与「从池借还」，量化建连开销</li>
 *   <li>LRU 缓存：对比「每次查库」与「命中进程内缓存」，量化省掉的数据库往返</li>
 * </ul>
 *
 * <p><b>怎么保证数字可信</b>
 * <ul>
 *   <li>JIT 预热：每个场景先跑 {@link #WARMUP} 次不计入统计</li>
 *   <li>多轮取中位数：跑 {@link #ROUNDS} 轮，取 QPS 居中的那一轮</li>
 *   <li>池容量与并发对齐：默认池 10，若并发超过它就变成在测「排队」而不是「池」，
 *       因此并发 32 时须用 -Dcmp.pool.size=64 启动</li>
 *   <li>延迟用 System.nanoTime() 逐次采样，分位数由全部样本排序得出，不是平均值估算</li>
 * </ul>
 *
 * <p><b>局限性（必须连同数字一起说明）</b>
 * 压测客户端、MySQL、被测服务跑在同一台机器上，三者争抢 CPU 与内存带宽；
 * 且本机 MySQL 走 localhost TCP 回环，建连开销低于真实跨机部署。
 * 因此绝对值不能外推到生产，只有同一环境下的相对差异有意义。
 *
 * <p>用法：{@code java -Dcmp.pool.size=64 Benchmark <场景>}
 */
public class Benchmark {

    static final String JDBC = System.getProperty("cmp.jdbc",
            "jdbc:mysql://localhost:3306/cmp_2?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=utf8&allowPublicKeyRetrieval=true");
    static final String USER = System.getProperty("cmp.db.user", "root");
    static final String PWD = System.getProperty("cmp.db.password", "123456");

    static final String SQL_ITEM = "SELECT listprice FROM item WHERE itemid = ?";
    static final String SQL_CAT = "SELECT catid, name, descn FROM category";
    static final String ITEM_ID = "EST-1";

    static final int WARMUP = 50;
    static final int ROUNDS = 3;

    // ---------- 操作抽象 ----------

    interface Op {
        void exec() throws Exception;
    }

    static final class Sample {
        final long[] lat;
        final long wall;
        final long errors;
        Sample(long[] lat, long wall, long errors) {
            this.lat = lat; this.wall = wall; this.errors = errors;
        }
    }

    // ---------- 四种被测操作 ----------

    /**
     * 防止 JIT 把被测逻辑判定为"无副作用"而整体消除。
     * 微基准里如果结果没人用，热点编译后整个循环会被删掉，测出来的就是空循环的纳秒数。
     */
    static long SINK = 0;

    /** 每次新建物理连接，再查一次 —— 代表没有连接池的原始实现 */
    static Op noPoolItem() {
        return () -> {
            Connection c = DriverManager.getConnection(JDBC, USER, PWD);
            try {
                PreparedStatement ps = c.prepareStatement(SQL_ITEM);
                ps.setString(1, ITEM_ID);
                ResultSet rs = ps.executeQuery();
                rs.next();
                SINK += rs.getBigDecimal(1).unscaledValue().longValue();
                rs.close();
                ps.close();
            } finally {
                c.close();
            }
        };
    }

    /** 从池借一个查询再归还 */
    static Op poolItem() {
        return () -> {
            Connection c = ConnectionPool.getInstance().getConnection();
            try {
                PreparedStatement ps = c.prepareStatement(SQL_ITEM);
                ps.setString(1, ITEM_ID);
                ResultSet rs = ps.executeQuery();
                rs.next();
                SINK += rs.getBigDecimal(1).unscaledValue().longValue();
                rs.close();
                ps.close();
            } finally {
                c.close();
            }
        };
    }

    /** 每次查库取全部分类 —— 代表接入缓存之前的实现 */
    static Op noCacheCategory() {
        return () -> {
            Connection c = ConnectionPool.getInstance().getConnection();
            try {
                PreparedStatement ps = c.prepareStatement(SQL_CAT);
                ResultSet rs = ps.executeQuery();
                long h = 0;
                while (rs.next()) {
                    h += rs.getString(1).hashCode();
                    h += rs.getString(2).hashCode();
                }
                SINK += h;
                rs.close();
                ps.close();
            } finally {
                c.close();
            }
        };
    }

    // 说明：这里刻意不提供"缓存命中"的基准。
    // 命中路径是纯读取，JIT 既能把它内联掉、也能把重复调用合并成一次，
    // 实测出来是 10ns/次、6 亿 req/s 这种明显失真的数字。
    // 因此只测"查库一次要多少成本"，缓存收益由它推导，不做倍数宣称。

    // ---------- 执行框架 ----------

    static Sample runOnce(int threads, int iters, Op op) throws Exception {
        final long[] lat = new long[threads * iters];
        final AtomicLong errors = new AtomicLong();
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);
        List<Thread> list = new ArrayList<Thread>();

        for (int t = 0; t < threads; t++) {
            final int tid = t;
            Thread th = new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < iters; i++) {
                        long t0 = System.nanoTime();
                        try {
                            op.exec();
                            lat[tid * iters + i] = System.nanoTime() - t0;
                        } catch (Throwable e) {
                            errors.incrementAndGet();
                            lat[tid * iters + i] = -1L;
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }, "bench-" + t);
            th.setDaemon(true);
            th.start();
            list.add(th);
        }

        long w0 = System.nanoTime();
        start.countDown();
        done.await();
        long wall = System.nanoTime() - w0;

        if (errors.get() > 0) {
            System.out.println("    [警告] 本场景失败 " + errors.get() + " 次");
        }
        return new Sample(lat, wall, errors.get());
    }

    /** 分位数：对全部样本升序排序后按下标取，失败样本（-1）先剔除 */
    static long pct(long[] lat, double p) {
        long[] v = new long[lat.length];
        int n = 0;
        for (long x : lat) if (x >= 0) v[n++] = x;
        if (n == 0) return 0;
        v = Arrays.copyOf(v, n);
        Arrays.sort(v);
        int idx = (int) Math.ceil(p / 100.0 * n) - 1;
        if (idx < 0) idx = 0;
        if (idx >= n) idx = n - 1;
        return v[idx];
    }

    static double qps(Sample s, int threads, int iters) {
        return (threads * iters) / (s.wall / 1e9);
    }

    static double mean(long[] lat) {
        long sum = 0, n = 0;
        for (long x : lat) if (x >= 0) { sum += x; n++; }
        return n == 0 ? 0 : sum / (double) n;   // 纳秒
    }

    /** 延迟自适应单位：低于 1ms 用微秒显示，否则会全变成 0.00ms 看不出差异 */
    static String latStr(double nanos) {
        if (nanos < 1.0e6) {
            return String.format("%8.2fμs", nanos / 1000.0);
        }
        return String.format("%8.2fms", nanos / 1.0e6);
    }

    /** 跑满 ROUNDS 轮，取 QPS 中位数的那一轮作为代表输出 */
    static void bench(String name, int threads, int iters, Op op, String unit) throws Exception {
        bench(name, threads, iters, op, unit, 1);
    }

    /**
     * @param opsPerSample 一个"样本"实际包含多少次业务操作。
     *                     单次耗时短于计时器分辨率时必须 &gt; 1，否则测到的只是时钟刻度。
     */
    static void bench(String name, int threads, int iters, Op op, String unit,
                      int opsPerSample) throws Exception {
        // 预热
        Sample warm = runOnce(threads, WARMUP, op);

        Sample[] ss = new Sample[ROUNDS];
        double[] qs = new double[ROUNDS];
        long totalErrors = warm.errors;
        for (int r = 0; r < ROUNDS; r++) {
            ss[r] = runOnce(threads, iters, op);
            qs[r] = (threads * (double) iters * opsPerSample) / (ss[r].wall / 1e9);
            totalErrors += ss[r].errors;
        }

        int pick = 0;
        double[] sorted = qs.clone();
        Arrays.sort(sorted);
        double median = sorted[ROUNDS / 2];
        for (int r = 0; r < ROUNDS; r++) if (qs[r] == median) { pick = r; break; }

        Sample s = ss[pick];
        // 有失败样本时 QPS 与分位数都不可信：失败的请求提前返回，会把吞吐算高
        String flag = totalErrors > 0 ? String.format("   [失败 %d 次，数字不可信]", totalErrors) : "";
        System.out.printf("  %-22s 并发=%-3d  %9.1f %-8s  avg=%s  P95=%s  P99=%s%s%n",
                name, threads, median, unit,
                latStr(mean(s.lat) / opsPerSample),
                latStr(pct(s.lat, 95) / (double) opsPerSample),
                latStr(pct(s.lat, 99) / (double) opsPerSample),
                flag);
    }

    static String mb() {
        Runtime rt = Runtime.getRuntime();
        return String.format("%.0f", rt.maxMemory() / 1024.0 / 1024.0);
    }

    public static void main(String[] args) throws Exception {
        String scene = args.length > 0 ? args[0] : "all";

        System.out.println("=======================================================");
        System.out.println("  环境：CPU " + Runtime.getRuntime().availableProcessors()
                + " 核 / JDK " + System.getProperty("java.version")
                + " / 池容量 " + Integer.getInteger("cmp.pool.size", 10)
                + " / 堆上限 " + mb() + "MB");
        System.out.println("  说明：客户端、MySQL、被测代码同机运行，只比较相对差异");
        System.out.println("=======================================================");

        // 缓存场景需要先由 DAO 填充一次
        new mvc.persistence.Impl.CategoryDaoImpl().getCategoryList();

        if (scene.equals("pool") || scene.equals("all")) {
            System.out.println();
            System.out.println("[实验一] 连接池：每次新建物理连接 vs 从池借还");
            System.out.println("         操作：SELECT listprice FROM item WHERE itemid=?  (单行主键查询)");
            // 无池模式只跑低并发：每次建连都会留下一个 TIME_WAIT，本机动态端口仅 16384 个，
            // 8 并发以上几十秒就会把端口耗尽、连接被拒。这本身就是需要连接池的理由之一。
            int[][] lowCfg = {{1, 100}, {4, 80}};
            for (int[] c : lowCfg) {
                bench("无池·新建连接", c[0], c[1], noPoolItem(), "req/s");
                bench("有池·借还", c[0], c[1] * 30, poolItem(), "req/s");
            }
            // 高并发只测有池：池复用连接，不产生 TIME_WAIT
            bench("有池·借还", 8, 2000, poolItem(), "req/s");
            bench("有池·借还", 32, 1000, poolItem(), "req/s");
        }

        if (scene.equals("cache") || scene.equals("all")) {
            System.out.println();
            System.out.println("[实验二] 分类查询的数据库成本 —— 缓存要消除的就是这部分");
            System.out.println("         操作：SELECT catid,name,descn FROM category  (全表 " + catCount() + " 行)");
            System.out.println("         注：不做缓存命中的 QPS 对比。命中路径是纯读取，JIT 会把重复调用");
            System.out.println("             合并、耗时低于本机 100ns 计时分辨率，测出来的倍数没有意义。");
            int[][] cfg = {{1, 1000}, {8, 1000}, {32, 1000}};
            for (int[] c : cfg) {
                bench("每次查库", c[0], c[1], noCacheCategory(), "req/s");
            }
        }

        if (scene.equals("poolcap")) {
            System.out.println();
            System.out.println("[实验三] 池容量不足时的排队：固定并发 32，只改池容量");
            System.out.println("         操作：SELECT listprice FROM item WHERE itemid=?");
            bench("池容量=" + Integer.getInteger("cmp.pool.size", 10), 32, 400, poolItem(), "req/s");
        }

        ConnectionPool.getInstance().shutdown();
        System.out.println();
        System.out.println("完成。");
    }

    static int catCount() {
        try {
            Connection c = ConnectionPool.getInstance().getConnection();
            ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM category");
            rs.next();
            int n = rs.getInt(1);
            rs.close();
            c.close();
            return n;
        } catch (Exception e) {
            return -1;
        }
    }
}
