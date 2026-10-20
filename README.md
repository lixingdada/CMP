# CMP 码农天堂

商品零售管理系统。后端 Spring Boot + MyBatis，前端 Vue 3，前后端分离。

仓库里有两个模块：`legacy/` 是最早用 Servlet + JSP 写的，`boot-server/` 和 `boot-web/` 是后来重写的版本，现在主要维护后者。

## 功能

- 注册、登录、改密码、个人资料
- 商品分类，商品分页列表（支持按分类和关键字筛选），SKU 详情
- 购物车增删改
- 下单、订单列表、订单详情
- 操作日志

## 技术栈

| | |
|---|---|
| 后端 | Java 17、Spring Boot 3.2、MyBatis、HikariCP、PageHelper、JJWT、BCrypt、springdoc |
| 前端 | Vue 3、Vite 5、vue-router、axios |
| 数据库 | MySQL 8（11 张表） |
| 部署 | Docker 多阶段构建、Nginx 反向代理 |

## 目录

```
legacy/                     第一版，Servlet 4.0 + JSP + JDBC
  src/main/java/mvc/        Servlet、Service、DAO、连接池
  src/main/webapp/          JSP、CSS、图片、web.xml
boot-server/                第二版后端
  src/main/java/com/cmp/mall/
    controller/             5 个 Controller
    service/                业务逻辑，事务边界在这一层
    mapper/                 7 个 Mapper 接口，XML 在 resources/mapper
    entity/ dto/            实体和出入参
    security/               JWT 签发、拦截器、UserContext
    aop/ common/            日志切面、统一响应、异常处理、traceId
boot-web/                   第二版前端，7 个页面
deploy/                     Dockerfile、nginx.conf、compose 文件
test/                       测试和压测脚本
pom.xml                     聚合工程，一条命令构建两个 Java 模块
schema.sql                  建表语句和种子数据
```

## 快速开始

### Docker

```bash
cd deploy
cp .env.example .env      # 修改数据库密码和 JWT_SECRET
docker compose up -d --build
```

访问 http://localhost。compose 里配了 healthcheck，MySQL 就绪之后才启动后端，后端起来 Nginx 才开始转发。

### 本地开发

```bash
# Windows 的 mysql 客户端默认按 GBK 读文件，不加这个参数种子数据里的中文会报 1366
mysql --default-character-set=utf8mb4 -u root -p < schema.sql

cd boot-server && mvn spring-boot:run       # 8080，接口文档 /swagger-ui.html
cd boot-web && npm install && npm run dev   # 5173，/api 由 vite 代理到 8080
```

后端默认连 `localhost:3306/cmp_2`，数据库账号、密码、端口都可以用环境变量覆盖，见 `application.yml`。测试账号 `test / 123456`。

### 构建

```bash
mvn package
```

根目录是聚合工程，会依次构建 `legacy` 和 `boot-server`。产物：

- `legacy/target/cmp.war`：丢进 Tomcat 9 就是第一版。servlet-api 是 provided，其余依赖（JSTL、MySQL 驱动、fastjson2）都在 `WEB-INF/lib` 里。
- `boot-server/target/cmp-boot-server.jar`：`java -jar` 直接跑。

只构建其中一个用 `mvn -pl legacy package` 或 `mvn -pl boot-server package`。

## 接口

统一前缀 `/api`，响应格式 `{ code, message, data, traceId }`，`code` 为 0 表示成功。鉴权列标 ✓ 的接口需要在请求头带 `Authorization: Bearer <token>`。

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | /auth/register | 注册 | |
| POST | /auth/login | 登录，返回 token | |
| GET | /auth/me | 当前用户 | ✓ |
| PUT | /auth/password | 修改密码 | ✓ |
| GET | /categories | 分类列表 | |
| GET | /products | 商品分页，支持 categoryId、keyword | |
| GET | /products/{id} | 商品详情，含 SKU | |
| GET | /items/{id} | 单个 SKU | |
| GET | /cart | 购物车 | ✓ |
| POST | /cart/items | 加入购物车 | ✓ |
| PUT | /cart/items/{id} | 修改数量 | ✓ |
| DELETE | /cart/items/{id} | 删除一行 | ✓ |
| DELETE | /cart | 清空购物车 | ✓ |
| POST | /orders | 下单 | ✓ |
| GET | /orders | 订单分页 | ✓ |
| GET | /orders/{id} | 订单详情 | ✓ |
| GET/PUT | /profile | 个人资料 | ✓ |
| GET | /logs | 我的操作日志 | ✓ |

## 数据库

```
category ──< product ──< item          分类 → 商品 → SKU，库存在 item.qty 上
signon ── account ──< receiver         登录凭证 / 用户资料 / 收货地址
cart ──< cart_item                     购物车
orders ──< order_items                 订单，price 存下单时的价格
user_log                               操作日志
```

`orders`、`order_items`、`user_log` 的列名早期改过，驼峰和蛇形混在同一张表里（既有 `orderId` 也有 `user_id`），所以这几个 Mapper 全部写显式 `resultMap`，没开驼峰自动转换。

两个版本共用这一个库。第一版密码存明文，第二版登录时会自动升级成 BCrypt，升级之后第一版就查不到这个用户。要在两版之间来回切，先用 `schema.sql` 重建。

## 测试

```bash
bash test/run-tests.sh                       # 第一版，55 项
cd boot-server && mvn test                   # 第二版 Service 单测，21 项，不连库
bash boot-server/src/test/smoke-test.sh      # 第二版端到端，24 项，需要先起服务
```

冒烟脚本会重置库存、购物车、订单和测试账号，不要对着有真实数据的库跑。

## 部署

见 [deploy/README.md](deploy/README.md)。

## 性能

连接池、池容量、HTTP 端到端三组数据在 [PERF.md](PERF.md)，`bash test/bench.sh` 可以复现。

设计取舍和开发过程中踩过的坑记在 [NOTES.md](NOTES.md)。

