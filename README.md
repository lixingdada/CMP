# CMP · 码农天堂

一个商品零售系统，**用两种方式实现了两遍**：先用零框架的原生 Servlet + JDBC 手写一遍，再用 Spring Boot + MyBatis + Vue 3 重构一遍。

做两遍不是为了炫技，是因为只有把框架拆掉写过一次，才知道它到底替我们做了什么。

| | 版本一：手写版 | 版本二：框架版（当前主推） |
|---|---|---|
| 后端 | 原生 Servlet 4.0 + JSP + JDBC | Spring Boot 3.2 + MyBatis |
| 前端 | JSP 服务端渲染 | Vue 3 + Vite（前后端分离） |
| 路由 | 一个 URL 一个 Servlet，`web.xml` 注册 | `@RestController` + 注解映射 |
| 持久层 | 手写 DAO + JDBC 模板 | MyBatis Mapper + 动态 SQL |
| 连接池 | 自己写（动态代理归还） | HikariCP（知道它强在哪） |
| 事务 | `TransactionManager` 模板 + ThreadLocal | `@Transactional`（AOP 代理） |
| 登录态 | HttpSession | 无状态 JWT |
| 缓存 | 手写 LRU | 手写 LRU（保留，见下） |
| 部署 | Tomcat 单机 | Docker + Nginx 反代 + 双实例 |
| 代码量 | 70 个类 / 17 个 JSP / 约 9,400 行 | 后端 58 个类 + 7 个 Mapper，前端 8 个页面 |

两个版本都在这个仓库里：手写版在 `src/` 与 `web/`，框架版在 `boot-server/` 与 `boot-web/`，部署脚本在 `deploy/`。

---

## 技术栈

**后端**：`Java 17` `Spring Boot 3.2` `MyBatis` `HikariCP` `PageHelper` `JJWT` `BCrypt` `springdoc-openapi` `SLF4J + Logback`

**前端**：`Vue 3` `Vite 5` `vue-router` `axios`

**存储**：`MySQL 8`（11 张表）

**部署**：`Docker` 多阶段构建 `Docker Compose` `Nginx 1.27`（静态资源 + 反向代理 + 双实例负载）

---

## 目录结构

```
CMP/
├── src/  web/                 版本一：手写 Servlet + JSP（保留，作为对照）
│
├── boot-server/               版本二：Spring Boot 后端
│   ├── src/main/java/com/cmp/mall/
│   │   ├── controller/        5 个 REST 控制器
│   │   ├── service/           业务层，事务边界在这一层
│   │   ├── mapper/            7 个 Mapper 接口（按聚合根划分）
│   │   ├── entity/            11 张表对应的实体
│   │   ├── dto/               请求 / 响应对象（record + 普通类）
│   │   ├── security/          JWT 签发、登录拦截器、UserContext
│   │   ├── aop/               操作日志切面
│   │   ├── common/            统一响应体、错误码、全局异常处理、traceId
│   │   └── config/            数据源 / CORS / 拦截器 / OpenAPI / 密码编码器
│   ├── src/main/resources/
│   │   ├── application.yml    公共配置（数据源、MyBatis、JWT）
│   │   ├── application-dev.yml  本地开发：打印 SQL
│   │   ├── application-prod.yml 容器运行：不打印 SQL、关闭接口文档
│   │   └── mapper/*.xml       7 个 MyBatis 映射文件
│   └── src/test/smoke-test.sh 端到端冒烟测试（24 项断言）
│
├── boot-web/                  版本二：Vue 3 前端
│   └── src/
│       ├── views/             首页 / 商品详情 / 购物车 / 订单 / 登录 / 注册 / 个人中心
│       ├── api/index.js       axios 实例、token 注入、统一拆包、401 处理
│       └── router/index.js    路由与登录守卫
│
├── deploy/
│   ├── Dockerfile.server      多阶段：maven 构建 → 精简 JRE 运行
│   ├── Dockerfile.web        多阶段：node 构建 → nginx 托管
│   ├── nginx.conf             静态资源 + /api 反代 + 双实例 upstream
│   └── docker-compose.yml     mysql + server1 + server2 + web
│
├── test/                      测试与压测
│   ├── run-tests.sh           功能测试一键入口：拉依赖 → 编译 → 运行（55 项断言）
│   ├── LRUCacheTest.java      10 项：淘汰顺序、命中统计、容量边界、拒绝 null
│   ├── PoolContextTest.java   17 项：借还超时、ThreadLocal 绑定、事务回滚、池满排队
│   ├── E2ETest.java           28 项：全链路 + 前置数据重置，可反复执行
│   ├── bench.sh               压测一键入口：DAO 微基准 + 池容量 + HTTP 端到端
│   ├── Benchmark.java         连接池与查询成本基准（含防 JIT 消除与端口回收处理）
│   └── HttpBenchmark.java     HTTP 端到端基准，8 个接口 × 3 档并发
│
├── PERF.md                    性能压测报告（环境、局限、数据、可用与不可用的说法）
│
└── schema.sql                 11 张表建表语句 + 种子数据，测试账号 test / 123456
```

---

## 快速开始

### 方式一：Docker 一键启动（推荐）

需要 Docker Desktop。

```bash
cd deploy
cp .env.example .env        # 按需改密码与 JWT 密钥
docker compose up -d --build
```

启动后访问 <http://localhost> 。测试账号 `test / 123456`。

启动顺序由 healthcheck 保证：MySQL 就绪 → 两个后端实例就绪 → Nginx 才开始接流量。数据库首次启动会自动执行 `schema.sql`。

### 方式二：本地开发

```bash
# 1. 建库（库名 cmp_2）
mysql -u root -p < schema.sql

# 2. 后端（默认连 localhost:3306 / root / 123456，可用环境变量覆盖）
cd boot-server
mvn spring-boot:run
#   接口文档 http://localhost:8080/swagger-ui.html

# 3. 前端
cd boot-web
npm install
npm run dev
#   访问 http://localhost:5173（/api 由 Vite 代理到 8080）
```

---

## 接口一览

统一前缀 `/api`，统一响应体 `{ code, message, data, traceId }`，`code = 0` 表示成功。

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/auth/register` | 注册（同时创建 account 与购物车） | — |
| POST | `/auth/login` | 登录，返回 JWT | — |
| GET | `/auth/me` | 当前用户 | ✓ |
| PUT | `/auth/password` | 修改密码 | ✓ |
| GET | `/categories` | 分类列表 | — |
| GET | `/products?categoryId=&keyword=&page=&size=` | 商品分页 | — |
| GET | `/products/{productId}` | 商品详情（含 SKU） | — |
| GET | `/items/{itemId}` | 单个 SKU | — |
| GET | `/cart` | 购物车 | ✓ |
| POST | `/cart/items` | 加购（同商品累加） | ✓ |
| PUT | `/cart/items/{itemId}` | 改数量 | ✓ |
| DELETE | `/cart/items/{itemId}` | 移除 | ✓ |
| DELETE | `/cart` | 清空 | ✓ |
| POST | `/orders` | 下单 | ✓ |
| GET | `/orders?page=&size=` | 订单分页 | ✓ |
| GET | `/orders/{orderId}` | 订单详情 | ✓ |
| GET | `/profile` · PUT `/profile` | 个人资料 | ✓ |
| GET | `/logs?limit=` | 我的操作日志 | ✓ |

---

## 架构要点

### 前后端分离与无状态鉴权

登录成功后服务端签发 JWT，前端存在 `localStorage`，之后每个请求带 `Authorization: Bearer xxx`。服务端不保存会话。

这一点直接决定了部署形态：手写版用 HttpSession，部署两台 Tomcat 就必须解决会话共享（IP 粘滞或 session 复制）；改成 JWT 后两台实例只要共用同一个 `JWT_SECRET` 即可水平扩容，Nginx 可以放心用轮询。

前端路由守卫只负责体验（未登录时提前跳登录页），真正的权限判断在后端拦截器里——**前端校验永远是提示，不是防线**。

### 事务：下单为什么必须原子

一次下单要动五张表：`item`（扣库存）、`orders`（主表）、`order_items`（明细）、`cart_item`（清车）、`user_log`（日志）。任何一步失败都必须整体撤销，否则会出现"库存扣了但没有订单"。

框架版用 `@Transactional(rollbackFor = Exception.class)` 把边界划在 Service 层。`rollbackFor` 不能省——Spring 默认只对 `RuntimeException` 与 `Error` 回滚，抛受检异常时不回滚。

手写版没有这个注解，是用 `TransactionManager.execute(callback)` 手动包一层，并靠 `ThreadLocal` 把同一条连接传给各个 DAO。两种实现解决的是同一个问题：**事务边界必须由业务层决定，不能由某个 DAO 自己决定**。

### 库存不会超卖

扣库存不用"先查再改"，而是一条带条件的 UPDATE：

```sql
UPDATE item SET qty = qty - #{quantity}
WHERE itemid = #{itemId} AND qty >= #{quantity}
```

判断与扣减在同一行上完成，靠数据库行锁串行化并发请求。影响行数为 0 就是库存不足，由业务层抛异常触发回滚。这比"查出来判断再更新"安全得多——后者两个请求可能同时读到 10 件，各扣 8 件，最后变成 -6。

### 避开 N+1 查询

三处典型的 N+1 都改掉了：

- 商品列表要显示"最低价"，用 `LEFT JOIN + MIN` 聚合，而不是前端为每个商品再请求一次 SKU
- 购物车行要显示实时库存，用 `cart_item LEFT JOIN item` 一次查回
- 订单列表先分页查主表，再用一条 `IN` 查询把当页所有明细捞回来，在内存里分组

### 操作日志用 AOP，不用 Filter

手写版把 Filter 挂在订单入口上，只能拦到 URL 级别，日志内容还得在 Filter 里反查购物车。框架版改成 `@OperationLog` 注解 + `@AfterReturning` 切面：想在哪个业务动作上记日志就加一行注解，日志内容直接从方法参数取。

切面里对写日志本身做了 try-catch 兜底——审计失败不该让用户的下单请求报错。

### 密码：从明文迁移到 BCrypt

手写版 `signon.password` 存明文，登录时用 SQL 直接比对。改 BCrypt 后直接上线会让所有存量用户登录失败，所以登录逻辑做了兼容：判断存储值是否以 `$2a$` / `$2b$` / `$2y$` 开头，是就正常 `matches`，不是就按明文比对、通过后立刻用 BCrypt 覆盖。用户无感知，登录一次完成一次迁移。

### 分页

PageHelper 的实现是拦在 MyBatis 的 Executor 上：`startPage` 把参数放进当前线程的 ThreadLocal，下一次查询被改写加 `LIMIT`，并额外发一条 `COUNT`。所以 `startPage` 之后必须紧跟目标查询，中间不能插别的 SQL，否则分页参数会被错误消费。

商品查询因为有 `GROUP BY`，`COUNT` 语句被自动包成 `SELECT COUNT(0) FROM (...) table_count`，这一层是 PageHelper 自己处理的。

### 留着手写版的缓存

框架版没有引入 Redis，`util/LRUCache` 那套 `LinkedHashMap(accessOrder=true)` 的思路保留了下来。分类数据几十条、读多写极少、单机部署，上中间件是负收益。选型要匹配场景，不是越重越好。

---

## 版本一（手写版）值得单独说的三件事

手写版代码在 `src/`，完整说明见文件内注释。这里只留三个最容易被追问的点：

**连接池为什么用动态代理归还**

上层 60 多个调用点原本都是 `conn = getConnection()` … `conn.close()`。用 `Proxy` 包一层，把 `close()` 拦下来改成"归还到空闲队列"，接入连接池不需要改任何业务代码。池空时线程 `wait` 并设超时，避免请求无限挂起。

**连接的所有权归线程，不归 DAO**

直连时代连接用完即销毁，池化后它会被下一个请求复用。原来的代码里内层 DAO 一 `close()` 就把外层还在用的连接放回池。规则改成：请求级连接忽略 `close()`，真正的归一只发生在请求结束时（`ServletRequestListener`）。

**归还前必须重置会话状态**

老代码大量 `setAutoCommit(false)` 后不恢复。直连时无影响，池化后下一条 SQL 继承 `autoCommit=false`，**静默不提交**——不报错、数据就是没写进去。所以归还原处统一 `rollback()` + `setAutoCommit(true)`。

这三个坑都是加了连接池之后才暴露、光看代码看不出来的，是写测试跑出来的。

---

## 数据库

11 张表，三条主线：

```
category ──< product ──< item            商品：分类 → 商品 → SKU，库存记在 item
signon ── account ──< receiver           用户：凭证 / 资料 / 默认地址
cart ──< cart_item                       购物车
orders ──< order_items                   订单（price 为下单时刻快照）
user_log                                 操作日志（item_id / cart_id / order_id 多态关联）
```

建表与种子数据见 [`schema.sql`](schema.sql)。注意 `orders`、`order_items`、`user_log` 的列名混用了驼峰与蛇形（`orderId` 与 `user_id` 并存），所以所有映射都写成显式 `resultMap`，不依赖 MyBatis 的自动驼峰转换。

---

## 验证

测试全部在仓库内，可自行复现。

### 手写版：`bash test/run-tests.sh`

一条命令完成「拉依赖 → 编译主源码 → 编译测试 → 依次运行」，共 **55 项断言**：

| 用例 | 项数 | 覆盖内容 |
|---|---|---|
| `LRUCacheTest` | 10 | 淘汰顺序、命中统计、容量边界、拒绝 null、20 万次写读容量不溢出 |
| `PoolContextTest` | 17 | 借还与超时、ThreadLocal 绑定与解绑、归还后 `autoCommit` 重置、缓存命中不借连接、事务回滚与嵌套不重开、池满抛超时 |
| `E2ETest` | 28 | 登录 → 浏览 → 加购累加 → 下单 → 明细 → 日志全链路，含库存扣减、库存不足不扣成负数、事务整体回滚、连接不残留 |

`E2ETest` 进入正题前会执行 `prepareFixture` 把库存、购物车、订单、测试账号密码重置到确定状态，因此**可反复执行**（已连跑三次验证）。

> 一个跨版本的真实问题：手写版是明文比对密码的老实现，boot 版已改为 BCrypt 并在登录时自动升级存量明文。两版共用同一个库，boot 版登录一次后手写版就查不到该用户了。`prepareFixture` 会把测试账号密码还原为明文——这不是用例取巧，而是两个版本存储格式不兼容的直接体现。

### 框架版：`bash boot-server/src/test/smoke-test.sh`

**24 项断言全部通过**（JDK 17 + 真实 MySQL 8），覆盖登录与明文密码自动升级、无 token 返回 401、商品分页、加购累加、下单扣库存、库存不足整单回滚、越权校验、AOP 日志、参数校验。

另有：`mvn package` 全量 58 个类 0 错误；前端 `npm run build` 通过，产物 gzip 后主包 61.89 kB。

### 未验证的部分

- **Docker 镜像构建与容器编排**：开发机未安装 Docker。`docker-compose.yml` 已用 YAML 解析器校验结构、`nginx.conf` 按 Nginx 语法逐项核对，但镜像构建需要实际执行 `docker compose up --build` 才能确认。
- **高并发上限**：只压到 32 并发，没有做阶梯加压找容量拐点，因此无法给出"支持多少并发"的结论。

---

## 性能

见 **[PERF.md](PERF.md)**。数据全部为 `test/Benchmark.java` 与 `test/HttpBenchmark.java` 实跑所得，命令 `bash test/bench.sh`。

几个结论：

- 连接池把单行查询的固定开销从 **2.81 ms 压到 235 μs**（单并发吞吐 355 → 4256 req/s），省掉的是 TCP 握手与 MySQL 认证，与 SQL 无关
- 4 → 32 并发吞吐不再上升（7739 → 7481 req/s），说明瓶颈已转移到 MySQL 单机
- 池容量 10 打 32 并发：**吞吐不掉（7157 vs 7168）但 P99 由 11.20 ms 恶化到 78.23 ms**，只看平均值会完全错过
- 分类查询一次耗时 **287 μs**，接入进程内缓存后这部分从关键路径消失

**关于缓存倍数**：本仓库**不提供**"缓存使 QPS 提升 N 倍"这类数字。命中路径是纯读取，耗时低于本机 100 ns 计时分辨率且会被 JIT 合并重复调用，实测会得出 10 ns/次这种失真结果。详细说明见 PERF.md 第三节。

**关于绝对值**：压测机即开发机，MySQL 与压测客户端争抢同一颗 CPU，且表内只有十几行数据。绝对值不可外推生产，只有同环境下的相对差异成立。

---

## 已知不足

- Service 层通过构造器注入 Mapper，但还没有单元测试（只有端到端脚本），缺 Mock 测试
- 没有做分布式事务、消息队列、限流熔断——单机规模的系统上这些是过度设计
- 头像上传仍沿用文件系统存储，没有接对象存储
- 前端没有做 SSR，首屏依赖 JS 加载
