# 部署说明

```
浏览器
  │  :80
  ▼
┌─────────────────────────────────────────┐
│ web (nginx:1.27-alpine)                 │
│  ├── /            → /usr/share/nginx/html （Vue 构建产物）
│  └── /api/        → upstream cmp_backend （keepalive 长连接）
└─────────────────────────────────────────┘
  │ 轮询（无状态 JWT，无需会话粘滞）
  ├────────────────► server1:8080  ┐
  └────────────────► server2:8080  ┴─ HikariCP ─► mysql:3306
```

## 启动

```bash
cd deploy
cp .env.example .env      # 改密码与 JWT 密钥
docker compose up -d --build
docker compose ps         # 等 4 个服务都变成 healthy 再访问
```

访问 `http://localhost`，测试账号 `test / 123456`。

## 环境变量

| 变量 | 默认值 | 说明 |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | `cmp_root_pwd` | 数据库 root 密码 |
| `JWT_SECRET` | 内置默认值 | **必须改**。不少于 32 字节，不足则后端启动即失败 |
| `JWT_EXPIRE_MINUTES` | `120` | token 有效期 |
| `WEB_PORT` | `80` | 对外端口 |
| `DB_POOL_SIZE` | `15` | 单实例连接池上限。两实例合计 30，需小于 MySQL `max_connections` |

生成密钥：`openssl rand -base64 48`

## 常用命令

```bash
docker compose logs -f server1        # 看后端日志
docker compose up -d --build server1  # 只重建后端
docker compose down                   # 停止（保留数据）
docker compose down -v                # 停止并删除数据卷（下次启动会重新执行 schema.sql）
```

## 几个设计取舍

**为什么前后端同域**

生产环境由 Nginx 统一入口，`/api` 反代到后端，浏览器看到的是同源请求。这样 CORS 配置只在本地开发时起作用，生产环境不需要放开跨域，也就不用担心 `Access-Control-Allow-Origin` 配错导致的线上问题。

**为什么两个后端实例**

这个规模一个实例完全够用，上两个是为了把多实例才会碰到的问题暴露出来：会话怎么共享、上传的文件落到哪台机器、缓存怎么保持一致。

改成 JWT 之后第一个问题自己没了，这也是当初换 JWT 的主要原因。缓存里只放分类这种几乎不变的数据，两台各存一份不会不一致。以后要是缓存用户数据，就得换 Redis。

**为什么 MySQL 端口只绑 127.0.0.1**

`127.0.0.1:3307:3306` 是为了能本机用 DBeaver 连进去调试，同时不把数据库暴露到局域网。生产环境把这段删掉，让数据库只在容器网络内可达。

**为什么 Dockerfile 用两个阶段**

构建镜像里有 Maven、JDK、源码，运行镜像只需要一个 JRE 和一个 jar。分阶段之后最终镜像不含源码和构建工具，体积和攻击面都小得多。

层缓存也是按依赖变化顺序排的：先拷 `pom.xml` 单独跑 `dependency:go-offline`，再拷源码编译。只要依赖没变，改代码时不必重新下载几百兆依赖。

**为什么用 `MaxRAMPercentage` 而不是写死 `-Xmx`**

容器内存限额调整时 JVM 自动跟随。写死 `-Xmx512m` 的话，限额提到 2G 也用不上；写死 1G 而限额是 512M 则会被 OOM Killer 直接杀掉。

## 排错

| 现象 | 原因 |
|---|---|
| `web` 一直不启动 | 后端没到 healthy。`docker compose logs server1` 看是不是连不上 MySQL 或 JWT_SECRET 太短 |
| 页面能打开但接口 502 | Nginx 起来了但后端没起来，或 upstream 里服务名与 compose 服务名不一致 |
| 刷新子页面 404 | `nginx.conf` 里 `try_files ... /index.html` 被删了，history 路由需要它兜底 |
| 数据被重置 | 执行过 `docker compose down -v`，数据卷被删，下次启动重新执行 `schema.sql` |
| 订单时间差 8 小时 | 容器时区没设，检查 `TZ=Asia/Shanghai` 与 MySQL 的 `--default-time-zone` |
