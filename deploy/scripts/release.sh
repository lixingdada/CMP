#!/usr/bin/env bash
# 一键发布：构建镜像 → 重建容器 → 等健康检查 → 失败回滚上一版
#
#   ./release.sh              完整发布
#   ./release.sh --check       只做前置检查，不动容器
#   ./release.sh --no-cache    不用构建缓存
#   ./release.sh --rollback    回滚到上一版镜像
#
# 退出码：0 成功，1 失败（已尝试回滚），2 前置检查不通过
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DEPLOY_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
ROOT_DIR=$(cd "$DEPLOY_DIR/.." && pwd)
COMPOSE_FILE="$DEPLOY_DIR/docker-compose.yml"
ENV_FILE="$DEPLOY_DIR/.env"
LOG_DIR="$DEPLOY_DIR/logs"
IMAGE="cmp-server"
TAG="latest"
HEALTH_TIMEOUT=${HEALTH_TIMEOUT:-180}
CONTAINERS="cmp-server1 cmp-server2"

mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/release-$(date +%Y%m%d_%H%M%S).log"
exec > >(tee -a "$LOG_FILE") 2>&1

CHECK_ONLY=0
NO_CACHE=0
DO_ROLLBACK=0
for arg in "$@"; do
    case "$arg" in
        --check) CHECK_ONLY=1 ;;
        --no-cache) NO_CACHE=1 ;;
        --rollback) DO_ROLLBACK=1 ;;
        -h|--help) sed -n '2,10p' "$0"; exit 0 ;;
        *) echo "未知参数: $arg" >&2; exit 2 ;;
    esac
done

info() { printf '[%s] %s\n' "$(date +%H:%M:%S)" "$*"; }
warn() { printf '[%s] 警告: %s\n' "$(date +%H:%M:%S)" "$*" >&2; }
die()  {
    local msg="$1" code="${2:-1}"
    printf '[%s] 错误: %s\n' "$(date +%H:%M:%S)" "$msg" >&2
    exit "$code"
}

dc() { docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" "$@"; }

# ---------- 前置检查 ----------

check_env() {
    local ok=0

    command -v docker >/dev/null 2>&1 || { warn "找不到 docker"; return 1; }
    docker info >/dev/null 2>&1 || { warn "docker 守护进程没起来（Linux 上试 sudo systemctl start docker）"; return 1; }
    docker compose version >/dev/null 2>&1 || { warn "docker compose 插件未安装"; return 1; }

    if [ ! -f "$ENV_FILE" ]; then
        cp "$DEPLOY_DIR/.env.example" "$ENV_FILE"
        warn "没有 .env，已从 .env.example 复制一份，里面的密码和密钥上线前必须改"
    fi

    # shellcheck disable=SC1090
    set -a; . "$ENV_FILE"; set +a

    local secret=${JWT_SECRET:-}
    if [ ${#secret} -lt 32 ]; then
        warn "JWT_SECRET 少于 32 字节，后端会启动失败（openssl rand -base64 48 生成一个）"
        ok=1
    fi
    if [ "$secret" = "cmp-mall-change-this-secret-in-production-please-0123456789" ]; then
        warn "JWT_SECRET 还是示例值，任何人都能伪造 token"
    fi
    if [ "${MYSQL_ROOT_PASSWORD:-}" = "cmp_root_pwd" ]; then
        warn "MYSQL_ROOT_PASSWORD 还是示例值"
    fi

    [ -f "$ROOT_DIR/schema.sql" ] || { warn "找不到 schema.sql，首次启动会没有表"; ok=1; }

    # 构建两个镜像大概要 1.5G 空间，不够会卡在中途失败
    local avail
    avail=$(df -Pk "$DEPLOY_DIR" | awk 'NR==2 {print int($4/1024/1024)}')
    if [ -n "$avail" ] && [ "$avail" -lt 2 ]; then
        warn "可用磁盘只剩 ${avail}G，构建镜像建议留 2G 以上"
        ok=1
    fi

    local port=${WEB_PORT:-80}
    if command -v ss >/dev/null 2>&1; then
        if ss -ltn "sport = :$port" 2>/dev/null | grep -q LISTEN; then
            # 容器本来就在跑的时候端口被自己占着是正常的
            if [ -z "$(docker ps -q --filter "publish=$port" 2>/dev/null)" ]; then
                warn "端口 $port 已被别的进程占用"
                ok=1
            fi
        fi
    fi

    return $ok
}

# ---------- 健康检查 ----------

container_health() {
    docker inspect --format \
        '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' \
        "$1" 2>/dev/null || echo "missing"
}

wait_healthy() {
    local deadline=$((SECONDS + HEALTH_TIMEOUT))
    local s1 s2
    while [ "$SECONDS" -lt "$deadline" ]; do
        s1=$(container_health cmp-server1)
        s2=$(container_health cmp-server2)
        info "健康状态 server1=$s1 server2=$s2"
        case "$s1$s2" in
            healthyhealthy) return 0 ;;
            *unhealthy*) return 1 ;;
        esac
        sleep 5
    done
    warn "等待健康检查超时（${HEALTH_TIMEOUT}s）"
    return 1
}

probe_http() {
    local port=${WEB_PORT:-80}
    if command -v curl >/dev/null 2>&1; then
        # 环回地址不走代理，否则代理挂了会把发布判成失败
        curl -s -o /dev/null --max-time 8 --noproxy '*' -w '%{http_code}' \
            "http://127.0.0.1:${port}/api/categories" 2>/dev/null || echo 000
    else
        echo skip
    fi
}

# ---------- 回滚 ----------

rollback() {
    if ! docker image inspect "$IMAGE:previous" >/dev/null 2>&1; then
        die "没有上一版镜像，无法回滚（首次发布只能看日志排查）"
    fi
    info "回滚：$IMAGE:previous → $IMAGE:$TAG"
    docker tag "$IMAGE:previous" "$IMAGE:$TAG"
    dc up -d --no-build --force-recreate $CONTAINERS
    if wait_healthy; then
        info "回滚完成，服务已恢复"
    else
        die "回滚后仍然不健康，需要人工介入"
    fi
}

# ---------- 主流程 ----------

info "发布目录 $ROOT_DIR"

if [ "$DO_ROLLBACK" = "1" ]; then
    rollback
    exit 0
fi

if ! check_env; then
    die "前置检查未通过" 2
fi
info "前置检查通过"
[ "$CHECK_ONLY" = "1" ] && { info "--check 模式，到此结束"; exit 0; }

# 记下当前版本，构建失败或者新版本起不来时能退回去
if docker image inspect "$IMAGE:$TAG" >/dev/null 2>&1; then
    docker tag "$IMAGE:$TAG" "$IMAGE:previous"
    info "已把当前镜像标记为 previous"
else
    info "首次发布，没有 previous 可用"
fi

BUILD_ARGS=()
[ "$NO_CACHE" = "1" ] && BUILD_ARGS+=(--no-cache)
info "开始构建镜像"
if ! dc build "${BUILD_ARGS[@]}" server1; then
    die "镜像构建失败，容器未改动"
fi

info "重建容器"
if ! dc up -d --force-recreate --remove-orphans; then
    warn "容器启动失败，准备回滚"
    dc logs --tail=60 --no-color $CONTAINERS >&2 || true
    rollback
    exit 1
fi

if ! wait_healthy; then
    warn "健康检查未通过，最近日志如下"
    dc logs --tail=80 --no-color $CONTAINERS >&2 || true
    rollback
    exit 1
fi

code=$(probe_http)
info "接口探测 /api/categories -> HTTP $code"
if [ "$code" != "skip" ] && [ "${code:0:1}" != "2" ]; then
    warn "反向代理后面的接口返回 $code，回滚"
    rollback
    exit 1
fi

info "发布完成"
dc ps
docker image ls "$IMAGE" --format '镜像 {{.Tag}}  {{.Size}}  创建于 {{.CreatedSince}}'
