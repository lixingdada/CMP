#!/usr/bin/env bash
# ============================================================
# 手写版（原生 Servlet + JDBC）集成测试运行脚本
#
# 用法：
#   1. 先确保 MySQL 已按 schema.sql 初始化（库名 cmp_2）
#   2. bash test/run-tests.sh
#
# 脚本做四件事：拉依赖 → 编译主源码 → 编译测试 → 依次运行
# ============================================================
set -u

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

LIB="test/lib"
OUT="build/classes"
TOUT="build/test-classes"
MIRROR="${MAVEN_MIRROR:-https://maven.aliyun.com/repository/public}"

# ---------- 0. 环境检查 ----------
# 只配了 JAVA_HOME、没加进 PATH 的情况挺常见，这里补一下
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    PATH="$JAVA_HOME/bin:$PATH"
fi
if ! command -v javac >/dev/null 2>&1; then
    echo "[错误] 找不到 javac，请安装 JDK 8+ 并配置 JAVA_HOME / PATH"
    exit 1
fi
echo "[环境] $(javac -version 2>&1)"
echo "[环境] $(java -version 2>&1 | head -1)"

# ---------- 1. 依赖 ----------
mkdir -p "$LIB"
fetch() {
    local name="$1" url="$2"
    if [ ! -f "$LIB/$name" ]; then
        echo "[下载] $name"
        curl -sL --max-time 180 -o "$LIB/$name" "$url" || { echo "[错误] $name 下载失败"; exit 1; }
    fi
}
fetch servlet-api.jar   "$MIRROR/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar"
fetch mysql-connector.jar "$MIRROR/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
fetch fastjson2.jar     "$MIRROR/com/alibaba/fastjson2/fastjson2/2.0.43/fastjson2-2.0.43.jar"

DEPS="$LIB/servlet-api.jar;$LIB/mysql-connector.jar;$LIB/fastjson2.jar"

# ---------- 2. 编译主源码 ----------
echo ""
echo "=========== 编译主源码 ==========="
rm -rf "$OUT" && mkdir -p "$OUT"
find src -name '*.java' > /tmp/cmp_srcs.txt
javac -encoding UTF-8 -nowarn -cp "$DEPS" -d "$OUT" @/tmp/cmp_srcs.txt || {
    echo "[错误] 主源码编译失败"; exit 1; }
echo "类文件数: $(find "$OUT" -name '*.class' | wc -l)"

# ---------- 3. 编译测试 ----------
echo ""
echo "=========== 编译测试 ==========="
rm -rf "$TOUT" && mkdir -p "$TOUT"
javac -encoding UTF-8 -nowarn -cp "$OUT;$DEPS" -d "$TOUT" test/*.java || {
    echo "[错误] 测试编译失败"; exit 1; }
echo "类文件数: $(find "$TOUT" -name '*.class' | wc -l)"

# ---------- 4. 运行 ----------
CP="$OUT;$TOUT;$DEPS"
FAILED=0

run() {
    local cls="$1"
    echo ""
    echo "=========== $cls ==========="
    java -Dfile.encoding=UTF-8 -cp "$CP" "$cls"
    local rc=$?
    if [ $rc -ne 0 ]; then
        echo "[失败] $cls 退出码 $rc"
        FAILED=$((FAILED + 1))
    fi
}

# 纯本地逻辑，不依赖数据库
run LRUCacheTest
# 以下三个需要 MySQL 已按 schema.sql 初始化
run PoolContextTest
run E2ETest

echo ""
echo "==================================================="
if [ $FAILED -eq 0 ]; then
    echo "  全部测试通过"
else
    echo "  有 $FAILED 个测试类失败"
fi
echo "==================================================="
exit $FAILED
