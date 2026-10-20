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

## 运维脚本

`scripts/` 下三个脚本，只依赖 bash 和 docker，不用装额外的东西。

### release.sh

前置检查 → 把当前镜像标记成 `previous` → 构建 → 重建容器 → 等健康检查 → 探接口。任何一步失败自动回滚到上一版镜像，退出码非 0。

```bash
./scripts/release.sh --check      # 只做检查，不动容器
./scripts/release.sh              # 发布
./scripts/release.sh --rollback   # 手动退回上一版
```

前置检查看五件事：docker 守护进程在不在、`.env` 有没有、`JWT_SECRET` 够不够 32 字节、磁盘够不够 2G、端口有没有被别的进程占。日志写到 `deploy/logs/release-*.log`。

### backup.sh

```bash
./scripts/backup.sh db               # mysqldump 单事务导出，gzip，附 md5
./scripts/backup.sh logs             # 归档最近 24 小时的容器日志
./scripts/backup.sh all --keep 7     # 两个都做，删掉 7 天前的
./scripts/backup.sh restore <文件>    # 恢复，默认要先确认
```

`--single-transaction` 在 InnoDB 上拿一致性快照，不锁表；没有它，备份期间的写入会让各表停在不同时间点，恢复出来对不上账。

导出后校验三件事：文件非空、gzip 完整、里面有建表语句。mysqldump 失败时管道照样会生成文件，不校验就会攒下一堆几十字节的空备份，等到要恢复才发现。

恢复前先给当前库做一份安全备份，目标库是空的就跳过。有宿主机客户端就直接用，没有就 `docker exec` 进 `cmp-mysql`。

### monitor.sh

```bash
./scripts/monitor.sh             # 负载、内存、磁盘、接口延迟、容器状态
./scripts/monitor.sh --quiet     # 正常不输出，异常打印并返回 1，给 cron 用
./scripts/monitor.sh --json      # 单行 JSON，给监控系统采集
```

阈值默认：磁盘 85%、内存 90%、1 分钟负载超过核数、接口超过 1500 ms 或非 2xx，都可以用环境变量覆盖。

定时任务见 `crontab.example`。

## 排错

| 现象 | 原因 |
|---|---|
| `web` 一直不启动 | 后端没到 healthy。`docker compose logs server1` 看是不是连不上 MySQL 或 JWT_SECRET 太短 |
| 页面能打开但接口 502 | Nginx 起来了但后端没起来，或 upstream 里服务名与 compose 服务名不一致 |
| 刷新子页面 404 | `nginx.conf` 里 `try_files ... /index.html` 被删了，history 路由需要它兜底 |
| 数据被重置 | 执行过 `docker compose down -v`，数据卷被删，下次启动重新执行 `schema.sql` |
| 订单时间差 8 小时 | 容器时区没设，检查 `TZ=Asia/Shanghai` 与 MySQL 的 `--default-time-zone` |
