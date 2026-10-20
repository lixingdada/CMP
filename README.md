# CMP 码农天堂

课程作业做完之后又重写了一遍的商品零售系统。第一版是原生 Servlet + JSP + JDBC，第二版用 Spring Boot + MyBatis + Vue 3 做前后端分离。两版都留在仓库里，因为第二版能写出来，基本靠第一版踩过的那些坑。

|  | 第一版 | 第二版（现在主要维护这个） |
|---|---|---|
| 后端 | Servlet 4.0 + JSP + JDBC | Spring Boot 3.2 + MyBatis |
| 前端 | JSP 服务端渲染 | Vue 3 + Vite |
| 持久层 | 手写 DAO + JDBC 模板 | MyBatis Mapper |
| 连接池 | 自己写 | HikariCP |
| 事务 | TransactionManager + ThreadLocal | @Transactional |
| 登录态 | HttpSession | JWT |
| 部署 | 本地 Tomcat | Docker + Nginx 反代 + 双实例 |

代码分布：`src/` 和 `web/` 是第一版，`boot-server/` 和 `boot-web/` 是第二版，`deploy/` 放部署脚本。

## 技术栈

后端 Java 17 / Spring Boot 3.2 / MyBatis / HikariCP / PageHelper / JJWT / BCrypt / springdoc。

前端 Vue 3 / Vite 5 / vue-router / axios。

数据库 MySQL 8，11 张表。部署用 Docker 多阶段构建加 Nginx。

## 目录

```
src/  web/            第一版，Servlet + JSP
boot-server/          第二版后端
  controller/         5 个 Controller
  service/            业务逻辑，事务边界在这一层
  mapper/             7 个 Mapper 接口 + resources/mapper 下的 xml
  entity/ dto/        实体和出入参
  security/           JWT 签发、拦截器、UserContext
  aop/ common/        日志切面、统一响应、异常处理、traceId
boot-web/             第二版前端，7 个页面
deploy/               Dockerfile、nginx.conf、compose 文件
test/                 测试和压测
schema.sql            建表 + 种子数据，测试账号 test / 123456
```

## 跑起来

Docker 方式：

```bash
cd deploy
cp .env.example .env     # 改一下数据库密码和 JWT 密钥
docker compose up -d --build
```

然后访问 http://localhost 。compose 里配了 healthcheck，MySQL 起完才起后端，后端起来了 Nginx 才接流量。

本地开发方式：

```bash
# Windows 的 mysql 客户端默认按 GBK 读文件，不加这个参数种子数据里的中文会报 1366
mysql --default-character-set=utf8mb4 -u root -p < schema.sql

cd boot-server && mvn spring-boot:run      # 8080，接口文档在 /swagger-ui.html
cd boot-web && npm install && npm run dev  # 5173，/api 由 vite 代理到 8080
```

后端默认连 `localhost:3306/cmp_2`，账号密码可以用环境变量覆盖，见 `application.yml`。

## 接口

统一前缀 `/api`，返回 `{ code, message, data, traceId }`，`code=0` 是成功。带 ✓ 的需要在请求头里放 `Authorization: Bearer <token>`。

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | /auth/register | 注册 | |
| POST | /auth/login | 登录，返回 token | |
| GET | /auth/me | 当前用户 | ✓ |
| PUT | /auth/password | 改密码 | ✓ |
| GET | /categories | 分类 | |
| GET | /products | 商品分页，支持 categoryId / keyword | |
| GET | /products/{id} | 商品详情连同 SKU | |
| GET | /items/{id} | 单个 SKU | |
| GET | /cart | 购物车 | ✓ |
| POST | /cart/items | 加购 | ✓ |
| PUT | /cart/items/{id} | 改数量 | ✓ |
| DELETE | /cart/items/{id} | 删除一行 | ✓ |
| DELETE | /cart | 清空 | ✓ |
| POST | /orders | 下单 | ✓ |
| GET | /orders | 订单分页 | ✓ |
| GET | /orders/{id} | 订单详情 | ✓ |
| GET/PUT | /profile | 个人资料 | ✓ |
| GET | /logs | 我的操作日志 | ✓ |

## 数据库

```
category ──< product ──< item          分类 → 商品 → SKU，库存挂在 item 上
signon ── account ──< receiver         账号凭证 / 资料 / 收货地址
cart ──< cart_item                     购物车
orders ──< order_items                 订单，price 存下单时的价格
user_log                               操作日志
```

`orders`、`order_items`、`user_log` 这几张表的列名早期改过，驼峰和蛇形混在一起（同一张表里既有 `orderId` 也有 `user_id`）。所以 mapper 里全部写的是显式 resultMap，没开驼峰自动转换。

## 实现上的一些决定

**登录态用 JWT 而不是 Session**

第一版用 HttpSession，想部署两台就要解决会话共享。改成 JWT 之后两台实例只要 `JWT_SECRET` 一样就能水平扩容，Nginx 直接轮询。

前端路由守卫只管体验，权限判断在后端拦截器里做。改过前端 bundle 就能绕过的东西不能当权限用。

**下单必须在一个事务里**

一次下单要动五张表：扣 item.qty、插 orders、插 order_items、清 cart_item、写 user_log。中间任何一步失败都要整体撤销，否则库存扣了订单没生成，对不上账。

第二版在 Service 方法上加 `@Transactional(rollbackFor = Exception.class)`。`rollbackFor` 要写，Spring 默认只对 RuntimeException 回滚，抛受检异常的时候不回滚，这个坑很容易踩。

第一版没有注解，用 `TransactionManager.execute(callback)` 包一层，靠 ThreadLocal 让几个 DAO 拿到同一条连接。

**扣库存靠条件更新，不是先查再改**

```sql
UPDATE item SET qty = qty - #{quantity}
WHERE itemid = #{itemId} AND qty >= #{quantity}
```

判断和扣减在同一条语句里，影响行数是 0 就说明库存不够。如果先 select 出来判断再 update，两个请求可能都读到 10 件，各扣 8 件，最后库存变成 -6。这个我一开始就是那么写的，做并发测试才看出来。

**N+1 改了三处**

- 商品列表要显示最低价，用 LEFT JOIN + MIN 聚合，不是前端拿到列表再逐个查 SKU
- 购物车每行要显示实时库存，`cart_item LEFT JOIN item` 一次查完
- 订单列表先分页查主表，再用一条 `IN` 把当页的明细全捞回来，在内存里按 orderId 分组

**操作日志从 Filter 换成 AOP**

第一版把 Filter 挂在下单入口上，只能拦到 URL，日志内容还得在 Filter 里重新查一遍购物车。第二版改成 `@OperationLog` 注解加切面，日志内容直接从方法参数拿。

写日志那段套了 try-catch，记录失败不应该让用户的下单失败。

**密码从明文迁到 BCrypt**

第一版 `signon.password` 存的是明文。直接改成 BCrypt 的话存量用户全部登录不了，所以登录时先看存储值是不是以 `$2a$` / `$2b$` / `$2y$` 开头：是就走 matches，不是就按明文比一次，通过之后立刻用 BCrypt 覆盖。用户感觉不到，登录一次迁一个。

**分页用 PageHelper**

它拦在 MyBatis 的 Executor 上，`startPage` 把参数塞进 ThreadLocal，下一次查询被改写成带 LIMIT 的，并且额外发一条 COUNT。所以 `startPage` 后面必须紧跟目标查询，中间插一条别的 SQL 分页参数就被错用了。

商品查询带 GROUP BY，COUNT 被包成 `SELECT COUNT(0) FROM (...) table_count`，这层是 PageHelper 自己处理的。

**缓存没有上 Redis**

分类只有几十条，读多写极少，单机部署。`util/LRUCache` 那套 LinkedHashMap 就够，多一个中间件多一份运维。以后要多实例部署的话再说。

## 第一版的连接池

第一版原来每次 `DriverManager.getConnection()` 新建物理连接，并发一高就撑不住。后来自己写了个池，`src/mvc/persistence/pool/` 下面。

**借还**：借出去的 Connection 用 `Proxy` 包一层，`close()` 被拦下来改成归还到空闲队列，其他方法原样转发。这样上层六十多个调用点一行都不用改。池空时线程在 idle 上 wait，超时抛异常，不让请求一直挂着。

**连接归谁**：这块改了三轮。直连的时候连接用完就销毁，问题不明显；池化之后连接会被下一个请求复用，原来 DAO 里内层 `close()` 一调，外层还在用的连接就回池了，可能被别的请求借走。最后定成请求级连接忽略 `close()`，真正的归一只在请求结束时做，由 `ServletRequestListener` 负责。

**autoCommit 残留**：这个 bug 最难查。老代码有些地方 `setAutoCommit(false)` 之后没恢复，直连时没影响，池化后下一条 SQL 继承了 `autoCommit=false`，执行成功但没有提交，不报错，数据就是没进去。所以还池之前统一 `rollback()` + `setAutoCommit(true)`。

这三个问题都是加了池才暴露的，光读代码看不出来,是跑测试跑出来的。

## 测试

都在仓库里，可以自己跑。

第一版：

```bash
bash test/run-tests.sh
```

一条命令拉依赖、编译、跑三个测试类：

| 类 | 项数 | 覆盖 |
|---|---|---|
| LRUCacheTest | 10 | 淘汰顺序、命中计数、容量边界、拒绝 null |
| PoolContextTest | 17 | 借还超时、ThreadLocal 绑定、归还后 autoCommit 重置、事务回滚、嵌套不重开、池满 |
| E2ETest | 28 | 登录浏览加购下单查日志全链路，含库存扣减、库存不足、整体回滚 |

E2ETest 开跑之前会先把库存、购物车、订单、测试账号密码重置（`prepareFixture`），不然第二次跑就挂了。我一开始就写死了库存 120、订单号 1，连跑第二次五项全红。

这里有个跨版本的问题：第一版是明文比对密码，第二版已经改成 BCrypt 并且在登录时自动升级。两版共用同一个库，第二版登录一次之后第一版就查不到这个用户了。所以 `prepareFixture` 会把密码还原成明文。这不是测试取巧，是两个版本存储格式本来就不兼容。

第二版：

```bash
cd boot-server && mvn test                    # Service 层单测，Mockito
bash boot-server/src/test/smoke-test.sh       # 起服务之后的端到端冒烟，24 项
```

单测 21 项，全部 mock Mapper 不连库：下单的库存校验与价格快照、购物车数量累加与老数据兼容、登录的明文升级与错误码统一。跑 `mvn test` 大概二十秒。

push 之后 GitHub Actions 会跑 `mvn package` 和 `npm run build`，这两条不过 PR 就别看了。

### 没验证的

Docker 镜像没有实际构建过，开发机没装 Docker。`docker-compose.yml` 用 YAML 解析器校验过结构，`nginx.conf` 对着语法文档逐项核过，但 `docker compose up --build` 没跑过。

并发只压到 32，没做阶梯加压，所以给不出"能撑多少并发"的结论。

## 性能

见 [PERF.md](PERF.md)，`bash test/bench.sh` 可以复现。

几个数字：连接池把单行查询的固定开销从 2.81 ms 压到 235 μs；4 并发到 32 并发吞吐不升反降（7739 → 7481），瓶颈跑到 MySQL 那边去了；池容量 10 面对 32 并发时吞吐几乎不变（7157 vs 7168），但 P99 从 11.20 ms 涨到 78.23 ms。

缓存那块我没给倍数。命中路径耗时低于本机计时粒度，还被 JIT 折叠掉了，测出来的数是假的，具体原因写在 PERF.md 第三节。

## 还没做的

- 头像还是存本地文件，没接对象存储
- 没有分布式事务、消息队列、限流熔断，单机项目上这些属于过度设计
- 前端没做 SSR，首屏等 JS
- Service 层有单测了，Controller 层没有
