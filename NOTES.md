# 开发笔记

写这个项目过程中的一些决定和踩过的坑，按主题记，不按时间。跟使用方式相关的内容在 README 里。

## 为什么留着第一版

第一版是课程作业，Servlet + JSP + JDBC，一把梭直接连库。后来想换成 Spring Boot，本来打算把第一版删掉，最后还是留着了：第二版很多地方是照着第一版的问题改的，删了之后就说不清为什么这么写。比如为什么 mapper 里全是手写 resultMap，为什么下单要单独抽一个事务方法，这些都能在第一版的代码里找到原因。

两版共用一个数据库，所以测试脚本里要把密码还原成明文，否则跑完第二版再跑第一版就登不进去。

## 下单的事务

一次下单动五张表：扣 `item.qty`、插 `orders`、插 `order_items`、清 `cart_item`、写 `user_log`。中间任何一步失败都得整体撤销，否则会出现库存扣了但没订单，或者订单生成了但购物车没清。

第二版在 Service 方法上挂 `@Transactional(rollbackFor = Exception.class)`。`rollbackFor` 不能省，Spring 默认只对 `RuntimeException` 和 `Error` 回滚，抛受检异常时不回滚，语义就跟预期不一样了。

第一版没有注解，自己写了个 `TransactionManager.execute(callback)` 包一层，靠 ThreadLocal 让几个 DAO 拿到同一条连接。东西是能用的，但每个需要事务的地方都要手动套一层，漏一个就是脏数据。

## 扣库存

```sql
UPDATE item SET qty = qty - #{quantity}
WHERE itemid = #{itemId} AND qty >= #{quantity}
```

判断和扣减放在同一条语句里，返回的影响行数是 0 就说明库存不够。一开始是 `select` 出来判断再 `update`，看着没问题，跑并发才暴露：两个请求同时读到 10 件，各扣 8 件，库存变成 -6。条件更新靠数据库的行锁把判断和写绑在一起，不用额外加锁。

## N+1

改了三处：

- 商品列表要显示最低价。原来是拿到列表再逐个查 SKU，改成 `LEFT JOIN` 加 `MIN` 聚合一次查完。
- 购物车每行要显示实时库存，`cart_item LEFT JOIN item`。
- 订单列表先分页查主表，再用一条 `IN` 把当页的明细全捞回来，在内存里按 orderId 分组。没有逐单查明细。

## 操作日志

第一版把 Filter 挂在下单入口，只能拦到 URL 级别，日志内容还得在 Filter 里把购物车重新查一遍。第二版换成 `@OperationLog` 注解加切面，日志内容直接从方法参数拿，想在哪个动作上记录就在那个方法上加一行。

写库那段套了 try-catch，日志记录失败不应该让用户下单失败。

## 密码迁移

`signon.password` 在第一版里是明文。直接换 BCrypt 的话存量用户全部登录不了，所以登录时先看存储值是不是 `$2a$` / `$2b$` / `$2y$` 开头，是就走 `matches`，不是就按明文比一次，通过之后立刻用 BCrypt 覆盖写回。用户无感，登录一次迁一个。

## PageHelper

它拦在 MyBatis 的 `Executor` 上，`startPage` 把分页参数塞进 ThreadLocal，下一次查询被改写成带 `LIMIT` 的，同时额外发一条 `COUNT`。所以 `startPage` 后面必须紧跟目标查询，中间插一条别的 SQL，分页参数就被那条 SQL 用掉了。

商品查询带 `GROUP BY`，`COUNT` 会被包成 `SELECT COUNT(0) FROM (...) table_count`，这一层是 PageHelper 自己处理的，不用手写。

## 缓存没上 Redis

分类表只有几十条，读多写极少，部署形态是单机。`util/LRUCache` 用一个 `LinkedHashMap(accessOrder=true)` 加 `removeEldestEntry` 就够了，多引一个中间件就多一份运维成本。以后要多实例部署，缓存内容也不是这种几乎不变的数据，那时候再换。

## 第一版的连接池

原来每次 `DriverManager.getConnection()` 都新建物理连接，并发一高就撑不住。池在 `src/mvc/persistence/pool/`。

**借还**：借出去的 `Connection` 用 `Proxy` 包一层，`close()` 被拦下来改成归还到空闲队列，其余方法原样转发。这样上层六十多个调用点一行都不用改。池空时线程在 idle 队列上等，超时抛异常，不让请求一直挂着。

**连接归谁**：这块来回改了三轮。直连的时候连接用完就销毁，问题不明显；池化之后连接会被下一个请求复用，DAO 里内层 `close()` 一调，外层还在用的连接就回池了，可能被别的请求借走。最后定成请求级连接忽略 `close()`，真正归还只在请求结束时做，由 `ServletRequestListener` 负责。用 listener 而不是 Filter，是因为它不依赖 URL 映射，静态资源请求也能被通知到。

**autoCommit 残留**：这个最难查。老代码里有地方 `setAutoCommit(false)` 之后没有恢复，直连时没影响，池化之后下一条 SQL 继承了 `autoCommit=false`，执行成功但没有提交，也不报错，数据就是没进去。所以还池之前统一 `rollback()` 再 `setAutoCommit(true)`。

这三个问题都是加了池之后才暴露的，光读代码看不出来，跑测试才现形。

## 测试数据要能重复跑

E2ETest 一开始把库存写死 120、订单号写死 1，第一次跑全绿，第二次五项全红。加了 `prepareFixture`，开跑之前先把库存、购物车、订单、账号密码重置一遍。

## 没做的部分

- Docker 镜像没有实际构建过，开发机没装 Docker。`docker-compose.yml` 用 YAML 解析器校验过结构，`nginx.conf` 对着文档逐项核过，但 `docker compose up --build` 没跑起来过。
- 压测只到 32 并发，没做阶梯加压，所以答不出"能撑多少并发"。
- 头像存本地文件，没接对象存储。
- 前端没做 SSR。
- Controller 层没有单测，只有 Service 层和端到端。
- 消息队列、限流熔断这些单机项目用不上，没加。
