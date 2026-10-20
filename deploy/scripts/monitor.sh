#!/usr/bin/env bash
# 资源与可用性巡检。给 cron 用的时候加 --quiet，只在有告警时输出。
#
#   ./monitor.sh            打印一张表
#   ./monitor.sh --json     输出 JSON，方便接监控系统
#   ./monitor.sh --quiet    正常时不输出，异常时打印并返回 1
#
# 阈值可以用环境变量覆盖：
#   DISK_MAX=90 MEM_MAX=90 LOAD_MAX=4 HTTP_MS_MAX=1500 ./monitor.sh
#
# 退出码：0 全部正常，1 有告警
set -uo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DEPLOY_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
ENV_FILE="$DEPLOY_DIR/.env"
LOG_DIR="$DEPLOY_DIR/logs"

FORMAT="table"
QUIET=0
case "${1:-}" in
    --json) FORMAT="json" ;;
    --quiet|-q) QUIET=1 ;;
    -h|--help) sed -n '2,14p' "$0"; exit 0 ;;
esac

mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/monitor.log"

if [ -f "$ENV_FILE" ]; then
    # shellcheck disable=SC1090
    set -a; . "$ENV_FILE"; set +a
fi
WEB_PORT=${WEB_PORT:-80}
DISK_MAX=${DISK_MAX:-85}
MEM_MAX=${MEM_MAX:-90}
LOAD_MAX=${LOAD_MAX:-}
HTTP_MS_MAX=${HTTP_MS_MAX:-1500}

ALERTS=()

# ---------- 采集 ----------

cpu_cores() {
    if command -v nproc >/dev/null 2>&1; then
        nproc
    elif [ -r /proc/cpuinfo ]; then
        grep -c '^processor' /proc/cpuinfo
    else
        echo 0
    fi
}

load_1m() {
    local s
    if [ -r /proc/loadavg ]; then
        awk '{print $1}' /proc/loadavg
        return
    fi
    # uptime 的格式各平台一致后半段：load average: 0.52, 0.58, 0.59
    s=$(uptime 2>/dev/null | sed -n 's/.*load averages*: *\([0-9.]*\).*/\1/p')
    [ -n "$s" ] && echo "$s" || echo "n/a"
}

mem_used_pct() {
    if [ -r /proc/meminfo ]; then
        # MemAvailable 是 3.14 才加的，旧内核和 Git Bash 的模拟环境里没有，
        # 缺了它只能退回 MemFree，否则分子算成 0，占用率会一直是 100%
        awk '/^MemTotal/ {t=$2}
             /^MemAvailable/ {a=$2}
             /^MemFree/ {f=$2}
             END {
                 if (a == "") a = f
                 if (t > 0 && a != "") printf "%.1f", (t-a)*100/t
                 else print "n/a"
             }' /proc/meminfo
        return
    fi
    if command -v wmic >/dev/null 2>&1; then
        wmic OS get FreePhysicalMemory,TotalVisibleMemorySize /value 2>/dev/null \
            | tr -d '\r' | awk -F= '
                /FreePhysicalMemory/ {f=$2}
                /TotalVisibleMemorySize/ {t=$2}
                END {if (t>0) printf "%.1f", (t-f)*100/t; else print "n/a"}'
        return
    fi
    echo "n/a"
}

disk_used_pct() {
    df -P "$DEPLOY_DIR" 2>/dev/null | awk 'NR==2 {gsub(/%/,"",$5); print $5}' | head -1
}

container_state() {
    local name="$1"
    if ! command -v docker >/dev/null 2>&1; then
        echo "no-docker"
        return
    fi
    docker inspect --format \
        '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' \
        "$name" 2>/dev/null || echo "missing"
}

http_probe() {
    if ! command -v curl >/dev/null 2>&1; then
        echo "skip 0"
        return
    fi
    # 请求要绕过 http_proxy：本机环回地址走代理既没意义，
    # 代理不通时还会把健康检查误判成接口故障
    curl -s -o /dev/null --max-time 5 --noproxy '*' \
        -w '%{http_code} %{time_total}' \
        "http://127.0.0.1:${WEB_PORT}/api/categories" 2>/dev/null || echo "000 0"
}

# ---------- 判定 ----------

CORES=$(cpu_cores)
LOAD=$(load_1m)

# 默认阈值取核数：1 分钟负载超过核数，说明已经有请求在排队
if [ -z "$LOAD_MAX" ]; then
    if [ "${CORES:-0}" -gt 0 ]; then LOAD_MAX=$CORES; else LOAD_MAX=4; fi
fi
MEM=$(mem_used_pct)
DISK=$(disk_used_pct)
read -r HTTP_CODE HTTP_TIME <<<"$(http_probe)"
S1=$(container_state cmp-server1)
S2=$(container_state cmp-server2)
[ -z "$DISK" ] && DISK="n/a"

# 延迟换成毫秒。curl 的 time_total 单位是秒，用 awk 算避免 bash 处理小数要外部命令
HTTP_MS=$(awk -v t="${HTTP_TIME:-0}" 'BEGIN {printf "%d", t*1000}')

[ "$DISK" != "n/a" ] && [ "${DISK%%.*}" -ge "$DISK_MAX" ] && \
    ALERTS+=("磁盘占用 ${DISK}%，超过阈值 ${DISK_MAX}%")

[ "$MEM" != "n/a" ] && [ "${MEM%%.*}" -ge "$MEM_MAX" ] && \
    ALERTS+=("内存占用 ${MEM}%，超过阈值 ${MEM_MAX}%")

if [ "$LOAD" != "n/a" ]; then
    if awk -v l="$LOAD" -v m="$LOAD_MAX" 'BEGIN {exit !(l > m)}'; then
        ALERTS+=("1 分钟负载 ${LOAD}，超过阈值 ${LOAD_MAX}（${CORES} 核）")
    fi
fi

case "$HTTP_CODE" in
    2*) [ "$HTTP_MS" -gt "$HTTP_MS_MAX" ] && \
            ALERTS+=("接口响应 ${HTTP_MS} ms，超过阈值 ${HTTP_MS_MAX} ms") ;;
    skip) ;;
    000) ALERTS+=("接口无响应（连接失败）") ;;
    *)   ALERTS+=("接口返回 HTTP $HTTP_CODE") ;;
esac

for pair in "cmp-server1:$S1" "cmp-server2:$S2"; do
    name=${pair%%:*}; st=${pair##*:}
    case "$st" in
        healthy|running) ;;
        no-docker) ;;
        *) ALERTS+=("容器 $name 状态 $st") ;;
    esac
done

# ---------- 输出 ----------

TS=$(date '+%F %T')
if [ "$FORMAT" = "json" ]; then
    esc() { printf '%s' "$1" | sed 's/\\/\\\\/g; s/"/\\"/g'; }
    printf '{"time":"%s","load":"%s","cores":%s,"mem_pct":"%s","disk_pct":"%s",' \
        "$TS" "$LOAD" "${CORES:-0}" "$MEM" "$DISK"
    printf '"http_code":"%s","http_ms":%s,"server1":"%s","server2":"%s","alerts":[' \
        "$HTTP_CODE" "$HTTP_MS" "$S1" "$S2"
    first=1
    for a in "${ALERTS[@]:-}"; do
        [ -z "$a" ] && continue
        [ "$first" = "1" ] || printf ','
        printf '"%s"' "$(esc "$a")"
        first=0
    done
    printf ']}\n'
    exit $([ ${#ALERTS[@]} -gt 0 ] && echo 1 || echo 0)
fi

if [ "$QUIET" = "1" ] && [ ${#ALERTS[@]} -eq 0 ]; then
    exit 0
fi

printf '时间     %s\n' "$TS"
printf '负载     %-8s (%s 核)\n' "$LOAD" "${CORES:-?}"
printf '内存     %-8s\n' "${MEM}%"
printf '磁盘     %-8s\n' "${DISK}%"
printf '接口     HTTP %-4s %s ms\n' "$HTTP_CODE" "$HTTP_MS"
printf '容器     server1=%s  server2=%s\n' "$S1" "$S2"

if [ ${#ALERTS[@]} -gt 0 ]; then
    printf '\n告警 %d 条：\n' "${#ALERTS[@]}"
    for a in "${ALERTS[@]}"; do
        printf '  - %s\n' "$a"
        printf '[%s] ALERT %s\n' "$TS" "$a" >> "$LOG_FILE"
    done
    exit 1
fi

printf '\n状态正常\n'
exit 0
