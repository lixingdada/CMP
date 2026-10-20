# CMP · 码农天堂

一个不依赖任何 Web 框架的商品零售系统，从零手写 MVC 分层、DAO 持久层、连接池与事务管理，用于理解框架到底替我们做了什么。

技术栈：`Java 17` `Servlet 4.0` `JSP` `JDBC` `MySQL 8` `Tomcat 9`（零框架依赖）

---

## 为什么要手写

主流做法是 `Spring Boot + MyBatis` 起步，但那样会跳过一整层认知：请求怎么进到方法、SQL 怎么映射成对象、连接从哪来、事务边界由谁划定。

这个项目刻意不用框架，把这些东西逐个写一遍：

| 框架里的概念 | 本项目的手写实现 |
|---|---|
| DispatcherServlet 路由分发 | 每个 URL 一个 `Servlet`，由 `web.xml` / `@WebServlet` 注册 |
| DAO / Mapper | `persistence` 包下 DAO 接口 + `Impl` 手写 JDBC 实现 |
| 依赖注入 | Service 内部 `new` 出 DAO 实现 |
| HikariCP 连接池 | `pool/ConnectionPool`：预创建 + 动态代理归还 + 等待超时 |
| 声明式事务 | `TransactionManager`：`ThreadLocal` 绑定连接 + 模板方法划定边界 |
| Spring Cache | `util/LRUCache`：`LinkedHashMap(accessOrder=true)` + 淘汰策略 |
| 拦截器 / AOP | `Filter` 拦截订单生成与支付入口，统一写操作日志 |
| 视图解析 | JSP + `RequestDispatcher.forward` |

---

## 功能

- 商品浏览：分类 → 商品 → 条目三级导航，关键字搜索
- 购物车：加购、改数量、删除、金额实时计算
- 下单支付：地址填写、订单生成、订单明细、历史订单查询
- 库存：条目库存查询与扣减，库存不足时不扣成负数
- 用户体系：注册、登录、图形验证码、个人资料、头像上传
- 操作日志：下单与支付行为通过 `Filter` 统一记录

---

## 目录结构

```
src/mvc/
├── domain/          领域实体（11 个）
├── persistence/     持久层
│   ├── *.java       DAO 接口
│   ├── Impl/        手写 JDBC 实现
│   ├── DBUtil.java  JDBC 工具类（连接池版）
│   ├── ConnectionContext.java   线程级连接上下文
│   ├── TransactionManager.java  事务模板
│   └── pool/ConnectionPool.java 手写连接池
├── service/         业务层
├── cache/           进程内缓存
├── util/            工具（LRU 缓存等）
└── web/
    ├── servlet/     控制层（Servlet + Filter）
    └── listener/    请求级连接释放监听器
web/                 JSP 页面与静态资源
schema.sql           建表语句 + 种子数据
```

规模：70 个 Java 类、17 个 JSP、约 9,400 行代码。

---

## 数据库

11 张表：

```
category ──< product ──< item            商品三级结构
signon ── account ──< receiver           用户凭证 / 资料 / 地址
cart ──< cart_item                       购物车
orders ──< order_items                   订单
user_log                                 操作日志（多态关联 item / cart / order）
```

建表与种子数据见 [`schema.sql`](schema.sql)，测试账号 `test / 123456`。

---

## 运行

1. MySQL 建库：`mysql -u root -p < schema.sql`（默认库名 `cmp_2`）
2. 按需覆盖连接参数，无需改代码：

   ```bash
   -Dcmp.jdbc.username=root
   -Dcmp.jdbc.password=你的密码
   -Dcmp.pool.size=10
   ```

3. IDEA 配置 Tomcat 9，部署 `web` 目录，访问 `/mainForm`

---

## 几个实现细节

**连接池为什么用动态代理**

上层 60 多个调用点原本都是 `conn = getConnection()` … `conn.close()` 的写法。用 `Proxy` 包一层，把 `close()` 拦下来改成"归还到空闲队列"，这样接入连接池不需要改动任何业务代码。池空时线程在空闲队列上 `wait`，超时抛异常而不是无限挂起，避免请求堆积。

**为什么连接绑在 ThreadLocal 上**

一个 HTTP 请求由容器分配一条工作线程，把连接绑到线程上，同一次请求内多个 DAO 就能复用同一条物理连接。更关键的是，这让"一次请求一个事务"成为可能——事务边界由请求线程决定，而不是由某个 DAO 决定。

**连接的所有权归谁**

直连时代连接用完即销毁，池化之后它会被下一个请求复用，所以必须明确"谁能归还"。这里的规则是：**连接的所有权在线程上下文，而不在拿到它的那个 DAO**。

原来的代码里，内层 DAO 一 `close()` 就把连接放回池，外层 DAO 还在用的连接可能立刻被别的请求借走；如果改成"每个 DAO 自己归还"，60 多个调用点又要逐个改。所以把 `close()` 语义改成"请求级连接忽略归还"，真正的归一只发生在请求结束时（`ConnectionReleaseListener`，`ServletRequestListener` 对包括静态资源在内的所有请求都会触发，比 Filter 更不容易漏）。进程内单线程场景（命令行、单测）显式调用 `DBUtil.releaseThreadConnection()` 即可。

**归还前为什么必须重置状态**

老代码里大量 `setAutoCommit(false)` 之后只 `commit()`、不恢复自动提交。直连时连接销毁，状态随之消失；池化后下一条 SQL 会继承 `autoCommit=false`，于是静默不提交——这类问题在测试里几乎看不出来，上线后表现为"偶发丢数据"。所以 `ConnectionPool.release()` 在放回空闲队列前统一 `rollback()` + `setAutoCommit(true)`。

**什么时候该用缓存**

分类数据读多写极少，首页每次请求都查一遍是纯浪费，加进程内缓存可以挡掉绝大部分查询。但没有引入 Redis——单机 Tomcat 部署，缓存对象只有几十条记录，上中间件只会增加运维负担。选型要匹配场景。

**LRU 怎么做到 O(1)**

`LinkedHashMap` 构造时把 `accessOrder` 设为 `true`，每次 `get` / `put` 会把节点移到链表尾部；再重写 `removeEldestEntry`，超过容量时自动淘汰链表头部（最久未使用）。增删查都退化成哈希表操作。

---

## 验证

以下不是"应该能跑"，是实际跑过的：

- **编译**：`javac -Xlint:all` 全量编译 70 个类，无错误无警告（JDK 17 + Servlet 4.0 API）
- **单元级**：连接池借还、请求级复用、归还后状态重置、池耗尽超时、事务回滚、嵌套事务不重复开启、缓存命中后不再查库 —— 17 项全部通过
- **链路级**：连真实 MySQL 跑完 登录 → 分类 → 商品 → 条目 → 加购 → 改数量 → 下单 → 订单明细 → 操作日志 → 事务回滚，28 项全部通过
- **连接用量**：整条链路只占用 1 条物理连接（`ConnectionPool{created=1, idle=1, inUse=0}`），改造前是每处 DAO 调用一条

---

## 已知不足

- Service 层直接 `new` DAO，没有依赖注入，单元测试不好 mock
- 密码仍是明文存储，应改为加盐哈希
- 没有引入日志框架，仍用 `System.out`
- 页面渲染耦合 JSP，未做前后端分离
- 库存扣减与订单插入还没合并到同一事务里（`updateInventoryQuantity` 目前独立提交），并发下单超卖需要靠行锁或乐观版本号解决

这些是刻意保留的——把框架拆掉之后，正好能看清它们各自解决了什么问题。
