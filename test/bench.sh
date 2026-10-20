#!/usr/bin/env bash
# ============================================================
# 性能压测入口
#
# 分三层，各自隔离变量，避免把不同层的结论混在一起：
#   1. DAO 层微基准  —— 连接池、LRU 缓存（进程内，不含网络与容器）
#   2. 池容量实验    —— 固定并发下只改池大小，看排队拐点
#   3. HTTP 端到端   —— 真实请求 Spring Boot，含 Tomcat 与 JSON 序列化
#
# 前置条件：
#   - 已按 schema.sql 初始化 cmp_2
#   - 跑第 3 层前需先启动 boot-server（默认 8080）
#
# 注意：无池模式每次新建连接都会留下一个 TIME_WAIT，Windows 动态端口默认仅 16384 个。
# 因此脚本在跑连接池实验前会等待端口回收，否则连接会被拒、数字全部作废。
# ============================================================
set -u

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

LIB="test/lib"
OUT="build/classes"
TOUT="build/test-classes"
BASE_URL="${BASE_URL:-http://localhost:8080}"

CP="$OUT;$TOUT;$LIB/servlet-api.jar;$LIB/mysql-connector.jar;$LIB/fastjson2.jar"

# ---------- 前置：依赖与编译 ----------
if [ ! -f "$LIB/mysql-connector.jar" ]; then
    echo "[提示] 依赖未就绪，先执行 test/run-tests.sh 拉取"
    exit 1
fi
if ! command -v javac >/dev/null 2>&1; then
    echo "[错误] 找不到 javac"
    exit 1
fi
if [ ! -d "$OUT" ]; then
    echo "[编译] 主源码未编译，先执行 test/run-tests.sh"
    exit 1
fi
mkdir -p "$TOUT"
javac -encoding UTF-8 -nowarn -cp "$OUT;$LIB/servlet-api.jar;$LIB/mysql-connector.jar;$LIB/fastjson2.jar" \
      -d "$TOUT" test/Benchmark.java test/HttpBenchmark.java || {
    echo "[错误] 压测代码编译失败"; exit 1; }

# ---------- 等待 TIME_WAIT 回收 ----------
wait_ports() {
    local limit="${1:-3000}" max="${2:-180}" waited=0
    local n
    n=$(netstat -an 2>/dev/null | grep -c "TIME_WAIT")
    if [ "$n" -le "$limit" ]; then
        echo "[端口] TIME_WAIT=$n，可以开始"
        return 0
    fi
    echo "[端口] TIME_WAIT=$n 超过 $limit，等待回收（最多 ${max}s）..."
    while [ "$waited" -lt "$max" ]; do
        sleep 10; waited=$((waited + 10))
        n=$(netstat -an 2>/dev/null | grep -c "TIME_WAIT")
        echo "       已等 ${waited}s  TIME_WAIT=$n"
        [ "$n" -le "$limit" ] && return 0
    done
    echo "[警告] 端口仍紧张（TIME_WAIT=$n），无池场景的失败率会偏高，结果需谨慎解读"
    return 0
}

echo ""
wait_ports 3000 120

# ---------- 第 1 层：DAO 微基准 ----------
# 并发到 32，池容量必须 ≥ 并发，否则测的是排队而不是池
echo ""
java -Dfile.encoding=UTF-8 -Dcmp.pool.size=64 -cp "$CP" Benchmark pool
java -Dfile.encoding=UTF-8 -Dcmp.pool.size=64 -cp "$CP" Benchmark cache

# ---------- 第 2 层：池容量 ----------
echo ""
echo "########## 池容量 10（默认）vs 64，固定并发 32 ##########"
wait_ports 3000 120
java -Dfile.encoding=UTF-8 -Dcmp.pool.size=10 -cp "$CP" Benchmark poolcap
java -Dfile.encoding=UTF-8 -Dcmp.pool.size=64 -cp "$CP" Benchmark poolcap

# ---------- 第 3 层：HTTP ----------
echo ""
if curl -s --noproxy '*' --max-time 5 -o /dev/null "$BASE_URL/api/health"; then
    java -Dfile.encoding=UTF-8 -cp "$CP" HttpBenchmark "$BASE_URL"
else
    echo "[跳过] 后端 $BASE_URL 未启动，第 3 层无法执行"
fi

echo ""
echo "压测完成。"
