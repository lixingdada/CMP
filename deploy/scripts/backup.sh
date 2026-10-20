#!/usr/bin/env bash
# 备份与恢复
#
#   ./backup.sh db                只备份数据库
#   ./backup.sh logs              只归档容器日志
#   ./backup.sh all               两个都做（给 cron 用的默认动作）
#   ./backup.sh restore <文件>    从 .sql.gz 恢复，恢复前会要求确认
#
# 选项：
#   --keep N     保留天数，默认 7，超期的备份自动删除
#   --dir DIR    备份目录，默认 deploy/backups
#   -y, --yes    restore 时跳过确认
#
# 退出码：0 成功，1 失败
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DEPLOY_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
ENV_FILE="$DEPLOY_DIR/.env"
LOG_DIR="$DEPLOY_DIR/logs"

ACTION="all"
KEEP=7
BACKUP_DIR="$DEPLOY_DIR/backups"
RESTORE_FILE=""
ASSUME_YES=0

while [ $# -gt 0 ]; do
    case "$1" in
        db|logs|all) ACTION="$1" ;;
        restore) ACTION="restore"; RESTORE_FILE="${2:-}"; shift ;;
        --keep) KEEP="$2"; shift ;;
        --dir) BACKUP_DIR="$2"; shift ;;
        -y|--yes) ASSUME_YES=1 ;;
        -h|--help) sed -n '2,15p' "$0"; exit 0 ;;
        *) echo "未知参数: $1" >&2; exit 1 ;;
    esac
    shift
done

mkdir -p "$BACKUP_DIR" "$LOG_DIR"
LOG_FILE="$LOG_DIR/backup.log"

info() { printf '[%s] %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG_FILE"; }
warn() { printf '[%s] 警告: %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG_FILE" >&2; }
die()  { printf '[%s] 错误: %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG_FILE" >&2; exit 1; }
err()  { printf '[%s] 错误: %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG_FILE" >&2; return 1; }

# ---------- 连接参数 ----------

if [ -f "$ENV_FILE" ]; then
    # shellcheck disable=SC1090
    set -a; . "$ENV_FILE"; set +a
fi
DB_HOST=${DB_HOST:-127.0.0.1}
DB_PORT=${DB_PORT:-3306}
DB_USER=${DB_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-${MYSQL_ROOT_PASSWORD:-}}
DB_NAME=${DB_NAME:-cmp_2}

# 宿主机有客户端就直接用，没有就往容器里钻
find_mysqldump() {
    if command -v mysqldump >/dev/null 2>&1; then
        command -v mysqldump
        return 0
    fi
    local p
    for p in \
        "/c/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe" \
        "/c/Program Files/MySQL/MySQL Server 5.7/bin/mysqldump.exe" \
        /usr/bin/mysqldump \
        /usr/local/mysql/bin/mysqldump; do
        [ -x "$p" ] && { echo "$p"; return 0; }
    done
    return 1
}

find_mysql() {
    local dump
    dump=$(find_mysqldump) || return 1
    echo "${dump%mysqldump*}mysql${dump##*mysqldump}"
}

dump_client() {
    local bin="$1"; shift
    if [ -n "$DB_PASSWORD" ]; then
        "$bin" -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" "$@"
    else
        "$bin" -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" "$@"
    fi
}

# ---------- 数据库备份 ----------

backup_db() {
    local ts file dump_bin
    ts=$(date +%Y%m%d_%H%M%S)
    file="$BACKUP_DIR/db_${DB_NAME}_${ts}.sql.gz"

    if dump_bin=$(find_mysqldump); then
        info "用 $dump_bin 导出 $DB_NAME"
        # --single-transaction 在 InnoDB 上拿一致性快照，不锁表；
        # 没有它备份期间的写入会让各张表处于不同时间点，恢复出来对不上账
        if ! dump_client "$dump_bin" \
                --single-transaction --quick --skip-lock-tables \
                --routines --triggers --events \
                --default-character-set=utf8mb4 \
                --set-gtid-purged=OFF \
                "$DB_NAME" 2>>"$LOG_FILE" | gzip -9 > "$file"; then
            rm -f "$file"
            err "mysqldump 执行失败，已删除残缺文件（详情见 $LOG_FILE）"
            return 1
        fi
    elif docker ps --format '{{.Names}}' 2>/dev/null | grep -qx cmp-mysql; then
        info "宿主机没有 mysqldump，改用容器 cmp-mysql"
        if ! docker exec cmp-mysql mysqldump -uroot -p"$DB_PASSWORD" \
                --single-transaction --quick --routines --triggers \
                --default-character-set=utf8mb4 --set-gtid-purged=OFF \
                "$DB_NAME" 2>>"$LOG_FILE" | gzip -9 > "$file"; then
            rm -f "$file"
            err "容器内 mysqldump 执行失败，已删除残缺文件（详情见 $LOG_FILE）"
            return 1
        fi
    else
        err "找不到 mysqldump，也没有运行中的 cmp-mysql 容器"
        return 1
    fi

    # 备份文件的正确性必须自己验一遍。mysqldump 失败时管道照样生成文件，
    # 不检查的话会留下一堆几十字节的空备份，等到要恢复才发现
    if [ ! -s "$file" ]; then
        rm -f "$file"
        err "备份文件为空: $file"
        return 1
    fi
    if ! gzip -t "$file" 2>/dev/null; then
        err "gzip 校验失败，文件已损坏: $file"
        return 1
    fi

    local tables size
    tables=$(gunzip -c "$file" | grep -c '^CREATE TABLE' || true)
    size=$(du -h "$file" | cut -f1)
    if [ "$tables" -lt 1 ]; then
        rm -f "$file"
        err "备份里没有建表语句，导出过程有问题，已删除该文件"
        return 1
    fi

    if command -v md5sum >/dev/null 2>&1; then
        md5sum "$file" | awk '{print $1}' > "$file.md5"
    fi
    info "数据库备份完成：$(basename "$file")  ${size}  ${tables} 张表"
    return 0
}

# ---------- 日志归档 ----------

backup_logs() {
    local ts file
    ts=$(date +%Y%m%d_%H%M%S)
    file="$BACKUP_DIR/logs_${ts}.tar.gz"

    if ! command -v docker >/dev/null 2>&1 || ! docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^cmp-'; then
        info "没有运行中的容器，跳过日志归档"
        return 0
    fi

    local tmp
    tmp=$(mktemp -d)
    # shellcheck disable=SC2064
    trap "rm -rf '$tmp'" RETURN

    docker compose -f "$DEPLOY_DIR/docker-compose.yml" --env-file "$ENV_FILE" \
        logs --no-color --timestamps --since 24h > "$tmp/containers.log" 2>&1 || true

    # 应用自己写的文件日志（容器内 /app/logs，映射出来才有）
    if [ -d "$DEPLOY_DIR/app-logs" ]; then
        find "$DEPLOY_DIR/app-logs" -name '*.log' -mtime -1 -exec cp {} "$tmp/" \; 2>/dev/null || true
    fi

    tar -czf "$file" -C "$tmp" .
    info "日志归档完成：$(basename "$file")  $(du -h "$file" | cut -f1)"
}

# ---------- 轮转 ----------

rotate() {
    local count
    count=$(find "$BACKUP_DIR" -maxdepth 1 -type f \
        \( -name 'db_*.sql.gz' -o -name 'logs_*.tar.gz' \) -mtime +"$KEEP" | wc -l)
    if [ "$count" -gt 0 ]; then
        find "$BACKUP_DIR" -maxdepth 1 -type f \
            \( -name 'db_*.sql.gz' -o -name 'logs_*.tar.gz' \) -mtime +"$KEEP" \
            -exec rm -f {} \; -exec rm -f {}.md5 \;
        info "清理了 $count 个超过 ${KEEP} 天的备份"
    fi
    info "当前保留 $(find "$BACKUP_DIR" -maxdepth 1 -type f \( -name 'db_*.sql.gz' -o -name 'logs_*.tar.gz' \) | wc -l) 个备份，占用 $(du -sh "$BACKUP_DIR" | cut -f1)"
}

# ---------- 恢复 ----------

do_restore() {
    [ -n "$RESTORE_FILE" ] || die "restore 后面要跟备份文件路径"
    [ -f "$RESTORE_FILE" ] || die "文件不存在: $RESTORE_FILE"
    gzip -t "$RESTORE_FILE" 2>/dev/null || die "不是有效的 gzip 文件"

    local tables
    tables=$(gunzip -c "$RESTORE_FILE" | grep -c '^CREATE TABLE' || true)
    info "待恢复文件 $(basename "$RESTORE_FILE")，包含 ${tables} 张表的建表语句"

    if [ "$ASSUME_YES" != "1" ]; then
        printf '恢复会覆盖 %s 库中的同名表，确认继续？[y/N] ' "$DB_NAME"
        local ans
        read -r ans
        case "$ans" in y|Y) ;; *) die "已取消" ;; esac
    fi

    local bin
    bin=$(find_mysql) || die "找不到 mysql 客户端"
    # 恢复前先把当前库备一份，恢复错了还能退回去。
    # 空库没什么可备的，跳过，免得在日志里刷一条误导性的错误
    local has_tables
    has_tables=$(dump_client "$bin" -N -B \
        -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME'" \
        2>/dev/null | tr -d '\r' || echo 0)
    if [ "${has_tables:-0}" -gt 0 ]; then
        backup_db >/dev/null || warn "恢复前的安全备份失败，继续恢复"
    else
        info "目标库是空的，跳过恢复前备份"
    fi

    gunzip -c "$RESTORE_FILE" | dump_client "$bin" --default-character-set=utf8mb4 "$DB_NAME"
    info "恢复完成"
}

# ---------- 入口 ----------

case "$ACTION" in
    db)      backup_db && rotate || exit 1 ;;
    logs)    backup_logs && rotate || exit 1 ;;
    all)     backup_db && backup_logs && rotate || exit 1 ;;
    restore) do_restore ;;
esac
