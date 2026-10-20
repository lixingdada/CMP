import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP 端到端基准测试：真实请求 Spring Boot 服务，走完整链路
 * （Tomcat 线程池 → 拦截器 → Controller → Service → MyBatis → HikariCP → MySQL → JSON 序列化）。
 *
 * <p>与 {@link Benchmark} 的区别：那个测的是进程内 DAO 调用，隔离了网络与容器；
 * 这个测的是用户真正感知到的耗时。两者都报，才能说清"优化作用在哪一层"。
 *
 * <p>用法：{@code java HttpBenchmark [baseUrl]}
 */
public class HttpBenchmark {

    static String BASE = "http://localhost:8080";

    static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** 发一次 GET，必须把 body 读完，否则连接无法复用、数字会失真 */
    static int get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        int len = resp.body() == null ? 0 : resp.body().length();
        if (resp.statusCode() != 200 || len == 0) {
            throw new IllegalStateException("HTTP " + resp.statusCode() + " len=" + len);
        }
        return len;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) BASE = args[0];

        System.out.println("=======================================================");
        System.out.println("  HTTP 端到端压测　目标：" + BASE);
        System.out.println("  CPU " + Runtime.getRuntime().availableProcessors()
                + " 核 / JDK " + System.getProperty("java.version"));
        System.out.println("  说明：压测客户端与被测服务同机，只比较接口之间的相对开销");
        System.out.println("=======================================================");

        // 先探活，避免把连接错误当成性能数据
        int probe = get("/api/categories");
        System.out.println("  探活成功，/api/categories 响应 " + probe + " 字节");
        System.out.println();

        String[][] cases = {
                {"/api/categories", "分类列表（5 行，最轻）"},
                {"/api/products?category=FISH&page=1&pageSize=10", "商品分页（COUNT + 分页查询）"},
                {"/api/items/EST-1", "条目详情（单行主键查询）"},
                {"/api/products/FI-SW-01", "商品详情（多表关联：商品+条目）"},
        };

        int[][] loads = {{1, 500}, {8, 500}, {32, 300}};

        for (int[] load : loads) {
            final int threads = load[0];
            final int iters = load[1];
            System.out.println("[并发 " + threads + "]");
            for (String[] c : cases) {
                final String path = c[0];
                Benchmark.bench(c[1], threads, iters, () -> get(path), "req/s");
            }
            System.out.println();
        }

        System.out.println("完成。");
    }
}
