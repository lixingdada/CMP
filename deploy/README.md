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

## 配置说明

### 两个后端实例

Nginx 用 upstream 轮询转发，登录态是 JWT，没有会话粘滞问题，两台机器 `JWT_SECRET` 一致即可。缓存里只有分类这种几乎不变的数据，两台各存一份不影响结果；如果缓存的是用户数据，就得换成 Redis。

### 前后端同域

浏览器只访问 Nginx，`/api` 由 Nginx 反代到后端，看到的是同源请求。CORS 只在本地开发时生效，生产环境不用放开跨域。

### 数据库端口绑定

`127.0.0.1:3307:3306` 是为了本机能用客户端连进去调试，同时不暴露到局域网。部署到服务器时把这段删掉，让数据库只在容器网络内可达。

### 两阶段构建

构建阶段带 Maven、JDK 和源码，运行阶段只有一个 JRE 和一个 jar。最终镜像不含源码和构建工具。层也是按依赖变化顺序排的：先拷 `pom.xml` 跑 `dependency:go-offline`，再拷源码编译，改代码不会重新下载依赖。

### `MaxRAMPercentage`

容器内存限额调整时 JVM 自动跟随，比写死 `-Xmx` 稳妥。写死 `-Xmx512m` 的话限额提到 2G 也用不上；写死 1G 而限额只有 512M 会被 OOM Killer 杀掉。

## 排错

| 现象 | 原因 |
|---|---|
| `web` 一直不启动 | 后端没到 healthy。`docker compose logs server1` 看是不是连不上 MySQL 或 JWT_SECRET 太短 |
| 页面能打开但接口 502 | Nginx 起来了但后端没起来，或 upstream 里服务名与 compose 服务名不一致 |
| 刷新子页面 404 | `nginx.conf` 里 `try_files ... /index.html` 被删了，history 路由需要它兜底 |
| 数据被重置 | 执行过 `docker compose down -v`，数据卷被删，下次启动重新执行 `schema.sql` |
| 订单时间差 8 小时 | 容器时区没设，检查 `TZ=Asia/Shanghai` 与 MySQL 的 `--default-time-zone` |
