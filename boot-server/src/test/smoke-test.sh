#!/usr/bin/env bash
# 端到端冒烟测试：走完整业务链路，逐项断言
# 前置：MySQL 已按 schema.sql 初始化，后端已在 8080 起来，PATH 里有 mysql 客户端
set -u
BASE="http://localhost:8080/api"
# 用函数而不是变量：变量里的引号在展开时不会被重新解析，--noproxy * 会被通配符展开
CURL() { curl -s --noproxy '*' --max-time 15 "$@"; }
export PYTHONIOENCODING=utf-8
PY=python

# mysql 客户端不一定在 PATH 里（Windows 装完通常不勾选加入 PATH），这里挨个找一下
find_mysql() {
  if command -v mysql >/dev/null 2>&1; then
    command -v mysql
    return
  fi
  for p in \
    "/c/Program Files/MySQL/MySQL Server 8.0/bin/mysql.exe" \
    "/c/Program Files/MySQL/MySQL Server 8.4/bin/mysql.exe" \
    "/usr/local/mysql/bin/mysql" \
    "/opt/homebrew/bin/mysql"; do
    if [ -x "$p" ]; then
      echo "$p"
      return
    fi
  done
}

MYSQL="${MYSQL:-$(find_mysql)}"
if [ -z "$MYSQL" ]; then
  echo "[错误] 找不到 mysql 客户端，可用 MYSQL 环境变量指定绝对路径后重跑"
  exit 1
fi

pass=0
fail=0

check() { # $1=描述 $2=实际 $3=期望
  if [ "$2" = "$3" ]; then
    echo "  [PASS] $1"
    pass=$((pass+1))
  else
    echo "  [FAIL] $1  => 实际:$2 期望:$3"
    fail=$((fail+1))
  fi
}

# 按 "['data']['token']" 这样的路径取值。用正则解析而不是 eval：
# eval 遇到路径里的单引号会和外层引号冲突，且 eval 本身也不该出现在测试脚本里
json_get() {
  $PY -c "
import sys, json, re
raw = sys.stdin.read().strip()
if not raw:
    print('')
    sys.exit(0)
d = json.loads(raw)
for m in re.finditer(r\"\['([^']+)'\]|\[(\d+)\]\", sys.argv[1]):
    d = d[m.group(1)] if m.group(1) is not None else d[int(m.group(2))]
print('' if d is None else d)
" "$1" 2>/dev/null
}

echo "=========== 1. 登录（同时验证明文密码自动升级为 BCrypt） ==========="
before=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select password from signon where username='test';" 2>/dev/null | grep -v Warning)
echo "  登录前 signon.password: ${before:0:20}..."

RESP=$(CURL -X POST "$BASE/auth/login" -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}')
TOKEN=$(echo "$RESP" | json_get "['data']['token']")
code=$(echo "$RESP" | json_get "['code']")
check "登录返回 code=0" "$code" "0"
[ -z "$TOKEN" ] && { echo "  [FATAL] 未拿到 token，后续跳过"; echo "$RESP"; exit 1; }
echo "  token: ${TOKEN:0:32}..."

after=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select password from signon where username='test';" 2>/dev/null | grep -v Warning)
echo "  登录后 signon.password: ${after:0:32}..."
case "$after" in
  \$2*) check "明文密码已自动升级为 BCrypt" "yes" "yes" ;;
  *) check "明文密码已自动升级为 BCrypt" "no" "yes" ;;
esac

AUTH="-H Authorization:Bearer\ $TOKEN"
api() { CURL -X "$1" "$BASE$2" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" ${3:+-d "$3"}; }

echo ""
echo "=========== 2. 鉴权 ==========="
noauth=$(CURL -o /dev/null -w "%{http_code}" "$BASE/cart")
check "未带 token 访问 /cart 返回 401" "$noauth" "401"
me=$(api GET /auth/me | json_get "['data']['userId']")
check "/auth/me 返回当前用户" "$me" "test"

echo ""
echo "=========== 3. 商品浏览（免登录） ==========="
total=$(api GET "/products?page=1&size=3" | json_get "['data']['total']")
check "商品总数 > 0" "$([ "${total:-0}" -gt 0 ] && echo yes)" "yes"
echo "  商品总数=$total"
firstProduct=$(api GET "/products?page=1&size=1" | json_get "['data']['list'][0]['productId']")
echo "  首个商品=$firstProduct"
items=$(api GET "/products/$firstProduct" | json_get "['data']['items'][0]['itemId']")
check "商品详情返回 SKU 列表" "$([ -n "$items" ] && echo yes)" "yes"
echo "  首个SKU=$items"

echo ""
echo "=========== 4. 购物车 ==========="
api DELETE /cart > /dev/null   # 清空，保证可重复执行
qtyBefore=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select qty from item where itemid='$items';" 2>/dev/null | grep -v Warning)
echo "  加购前库存: $qtyBefore"

r=$(api POST /cart/items "{\"itemId\":\"$items\",\"quantity\":2}" | json_get "['code']")
check "加入购物车 code=0" "$r" "0"
cartTotal=$(api GET /cart | json_get "['data']['totalQuantity']")
check "购物车数量=2" "$cartTotal" "2"
# 再次加购同一 SKU，应累加到 4 而不是新增一行
api POST /cart/items "{\"itemId\":\"$items\",\"quantity\":2}" > /dev/null
cartTotal2=$(api GET /cart | json_get "['data']['totalQuantity']")
check "重复加购累加到 4" "$cartTotal2" "4"
# 只统计当前用户的行：库里还存在历史脏数据（cart_id=1 且 item_id 为 NULL），
# 那是原版留下来的"第一代数据"，不能算进这次断言
lineCount=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select count(*) from cart_item ci join cart c on c.cart_id=ci.cart_id where c.username='test';" 2>/dev/null | grep -v Warning)
check "购物车行数=1（未重复插入）" "$lineCount" "1"

echo ""
echo "=========== 5. 下单（跨 5 张表的事务） ==========="
orderResp=$(api POST /orders '{"orderName":"张三","orderAddress":"湖南省长沙市岳麓区中南大学","orderTel":"13800000000"}')
ocode=$(echo "$orderResp" | json_get "['code']")
check "下单 code=0" "$ocode" "0"
orderId=$(echo "$orderResp" | json_get "['data']['orderId']")
orderAmount=$(echo "$orderResp" | json_get "['data']['totalAmount']")
echo "  订单号=$orderId 金额=$orderAmount"
check "订单号有效" "$([ -n "$orderId" ] && echo yes)" "yes"

qtyAfter=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select qty from item where itemid='$items';" 2>/dev/null | grep -v Warning)
expected=$((qtyBefore - 4))
check "库存已扣减 $qtyBefore -> $expected" "$qtyAfter" "$expected"

cartAfter=$(api GET /cart | json_get "['data']['totalQuantity']")
check "下单后购物车已清空" "$cartAfter" "0"

detailCount=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select count(*) from order_items where orderId=$orderId;" 2>/dev/null | grep -v Warning)
check "订单明细已写入" "$([ "${detailCount:-0}" -ge 1 ] && echo yes)" "yes"

echo ""
echo "=========== 6. 订单查询与越权校验 ==========="
ordersTotal=$(api GET "/orders?page=1&size=5" | json_get "['data']['total']")
check "订单列表总数 >= 1" "$([ "${ordersTotal:-0}" -ge 1 ] && echo yes)" "yes"
echo "  我的订单数=$ordersTotal"
d=$(api GET "/orders/$orderId" | json_get "['data']['orderId']")
check "订单详情可查" "$d" "$orderId"

echo ""
echo "=========== 7. 库存不足时应整单回滚 ==========="
api DELETE /cart > /dev/null
api POST /cart/items "{\"itemId\":\"$items\",\"quantity\":1}" > /dev/null
# 直接把库存改成 0，模拟超卖场景
"$MYSQL" -uroot -p123456 -e "use cmp_2; update item set qty=0 where itemid='$items';" 2>/dev/null
stockFail=$(api POST /orders '{"orderName":"李四","orderAddress":"测试地址","orderTel":"13900000000"}')
scode=$(echo "$stockFail" | json_get "['code']")
check "库存不足下单失败 code=2006" "$scode" "2006"
leftInCart=$(api GET /cart | json_get "['data']['totalQuantity']")
check "失败后购物车未被清空（整体回滚）" "$leftInCart" "1"
lostOrder=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select count(*) from orders where orderName='李四';" 2>/dev/null | grep -v Warning)
check "失败订单未落库（主表已回滚）" "$lostOrder" "0"

echo ""
echo "=========== 8. 操作日志（AOP 切面） ==========="
logCount=$(api GET "/logs?limit=5" | $PY -c "import sys,json;print(len(json.load(sys.stdin)['data']))" 2>/dev/null)
check "操作日志有记录" "$([ "${logCount:-0}" -ge 1 ] && echo yes)" "yes"
echo "  最近日志条数=$logCount"
actions=$("$MYSQL" -uroot -p123456 -N -e "use cmp_2; select group_concat(distinct action) from user_log where user_id='test';" 2>/dev/null | grep -v Warning)
echo "  已记录动作: $actions"

echo ""
echo "=========== 9. 分页与参数校验 ==========="
p2=$(api GET "/products?page=2&size=2" | json_get "['data']['pageNum']")
check "分页页码正确" "$p2" "2"
badPage=$(api GET "/products?page=1&size=9999" | json_get "['data']['pageSize']")
check "pageSize 被限制到 100" "$badPage" "100"
badReg=$(CURL -X POST "$BASE/auth/register" -H "Content-Type: application/json" -d '{"username":"ab","password":"123"}')
bcode=$(echo "$badReg" | json_get "['code']")
check "非法注册参数被拦截 code=1001" "$bcode" "1001"
badMsg=$(echo "$badReg" | json_get "['message']")
echo "  校验提示: $badMsg"

echo ""
echo "=========== 10. 恢复测试数据 ==========="
"$MYSQL" -uroot -p123456 -e "use cmp_2; update item set qty=$qtyBefore where itemid='$items'; delete ci from cart_item ci join cart c on c.cart_id=ci.cart_id where c.username='test';" 2>/dev/null
echo "  已还原库存 $items -> $qtyBefore，清空 test 的购物车"

echo ""
echo "==================================================="
echo "  通过 $pass 项，失败 $fail 项"
echo "==================================================="
[ "$fail" -eq 0 ] || exit 1
