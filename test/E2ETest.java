import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.domain.Category;
import mvc.domain.Item;
import mvc.domain.Order;
import mvc.domain.OrderItem;
import mvc.domain.Product;
import mvc.domain.User;
import mvc.persistence.DBUtil;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.persistence.Impl.CategoryDaoImpl;
import mvc.persistence.Impl.ItemDaoImpl;
import mvc.persistence.Impl.LogDaoImpl;
import mvc.persistence.Impl.OrderDaoImpl;
import mvc.persistence.Impl.ProductDaoImpl;
import mvc.persistence.Impl.ReceiverDaoImpl;
import mvc.persistence.Impl.UserDaoImpl;
import mvc.persistence.TransactionManager;
import mvc.persistence.pool.ConnectionPool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全链路集成测试：用真实 MySQL 跑浏览 → 购物车 → 下单 → 日志，验证 schema 与代码一致。
 *
 * <p><b>测试必须可重入。</b>本用例会改动库存、购物车、订单、登录密码，因此进入正题前先执行
 * {@link #prepareFixture}，把这些前置条件重置到确定状态；否则只有"刚执行完 schema.sql"的第一遍能通过。
 *
 * <p>关于密码：手写版是明文比对的老实现（{@code WHERE password = ?}），而 boot 版已改为 BCrypt
 * 并在登录时自动升级存量明文。两版共用同一个库，跑手写版用例前需把测试账号密码还原为明文，
 * 这是两个版本存储格式不兼容的真实体现，而非用例本身的问题。
 */
public class E2ETest {

    static int pass = 0, fail = 0;

    /** 测试基线：库存重置值 */
    static final int BASE_QTY = 120;

    static void check(String name, boolean cond) { check(name, cond, ""); }

    static void check(String name, boolean cond, String detail) {
        if (cond) { pass++; System.out.println("  PASS  " + name + (detail.isEmpty() ? "" : "  " + detail)); }
        else { fail++; System.out.println("  FAIL  " + name + (detail.isEmpty() ? "" : "  " + detail)); }
    }

    static Object scalar(String sql) throws Exception {
        DBUtil.releaseThreadConnection();
        Connection c = ConnectionPool.getInstance().getConnection();
        ResultSet rs = c.createStatement().executeQuery(sql);
        rs.next();
        Object v = rs.getObject(1);
        rs.close();
        c.close();
        return v;
    }

    static void exec(String sql) throws Exception {
        DBUtil.releaseThreadConnection();
        Connection c = ConnectionPool.getInstance().getConnection();
        c.createStatement().executeUpdate(sql);
        c.close();
        DBUtil.releaseThreadConnection();
    }

    /**
     * 把测试会触碰到的数据重置到已知状态，保证用例可以反复执行。
     */
    static void prepareFixture(String user) throws Exception {
        // 1. 库存回到基线，避免上一轮扣减残留
        exec("UPDATE item SET qty = " + BASE_QTY + " WHERE itemid = 'EST-1'");
        // 2. 清掉该用户的购物车（含明细），否则 C1/C3 的行数断言不成立
        exec("DELETE ci FROM cart_item ci JOIN cart c ON c.cart_id = ci.cart_id WHERE c.username = '" + user + "'");
        exec("DELETE FROM cart WHERE username = '" + user + "'");
        // 3. 清掉该用户的历史订单与明细，D1 的订单数才是确定的
        exec("DELETE oi FROM order_items oi JOIN orders o ON o.orderId = oi.orderId WHERE o.userId = '" + user + "'");
        exec("DELETE FROM orders WHERE userId = '" + user + "'");
        // 4. 密码还原为手写版能识别的明文格式（boot 版会把它再升级成 BCrypt）
        exec("UPDATE signon SET password = '123456' WHERE username = '" + user + "'");
    }

    public static void main(String[] args) throws Exception {
        final String USER = "test";
        DBUtil.releaseThreadConnection();
        prepareFixture(USER);

        // ---------- A. 用户体系 ----------
        UserDaoImpl userDao = new UserDaoImpl();
        User u = userDao.findUserByUsernameAndPassword(USER, "123456");
        check("A1 账号密码登录成功", u != null && USER.equals(u.getUsername()));
        User profile = new User();
        userDao.loadUser(profile, USER);
        check("A2 读取到用户资料", profile.getVirtualName() != null, "name=" + profile.getVirtualName());
        check("A3 用户存在性判断", userDao.userExists(USER));
        check("A4 收货地址查询", userDao.findAddressByUser(USER) != null && userDao.findAddressByUser(USER).size() >= 1);
        check("A5 收件人列表", new ReceiverDaoImpl().getReceiversByUserId(USER).size() >= 1);

        // ---------- B. 商品目录 ----------
        List<Category> cats = new CategoryDaoImpl().getCategoryList();
        check("B1 分类 5 条", cats.size() == 5, "size=" + cats.size());

        ProductDaoImpl productDao = new ProductDaoImpl();
        check("B2 分类下商品", productDao.getProductListByCategory("FISH").size() >= 2);
        check("B3 商品详情", productDao.getProduct("FI-SW-01") != null);
        check("B4 关键词搜索", productDao.searchProductList("鱼").size() >= 1);

        ItemDaoImpl itemDao = new ItemDaoImpl();
        List<Item> items = itemDao.getItemListByProduct("FI-SW-01");
        check("B5 商品下条目", items.size() >= 2, "size=" + items.size());
        Item item = itemDao.getItem("EST-1");
        check("B6 条目详情含价格", item != null && item.getListPrice() != null,
                item == null ? "" : "price=" + item.getListPrice());
        check("B7 库存查询返回真实数量", itemDao.getInventoryQuantity("EST-1") == BASE_QTY,
                "qty=" + itemDao.getInventoryQuantity("EST-1"));
        check("B8 条目名称查询", itemDao.getItemNameByItemId("EST-1") != null,
                "name=" + itemDao.getItemNameByItemId("EST-1"));
        check("B9 商品名查询", itemDao.getProductNameByItemId("EST-1") != null);
        check("B10 全量条目列表", itemDao.getItemsList().size() == 11);

        // ---------- B11. 库存扣减 ----------
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("itemid", "EST-1");
        m.put("increment", Integer.valueOf(2));
        itemDao.updateInventoryQuantity(m);
        check("B11 库存扣减生效", itemDao.getInventoryQuantity("EST-1") == BASE_QTY - 2,
                "qty=" + itemDao.getInventoryQuantity("EST-1"));

        Map<String, Object> over = new HashMap<String, Object>();
        over.put("itemid", "EST-1");
        over.put("increment", Integer.valueOf(99999));
        itemDao.updateInventoryQuantity(over);
        check("B12 库存不足不扣成负数", itemDao.getInventoryQuantity("EST-1") == BASE_QTY - 2,
                "qty=" + itemDao.getInventoryQuantity("EST-1"));

        // ---------- C. 购物车 ----------
        CartDaoImpl cartDao = new CartDaoImpl();
        if (cartDao.getCartByUserName(USER) == null) {
            cartDao.createCartForUser(USER);
        }
        Cart cart = cartDao.getCartByUserName(USER);
        check("C1 购物车创建/读取", cart != null && cart.getId() > 0, cart == null ? "" : "cartId=" + cart.getId());

        CartItem ci = new CartItem();
        ci.setItem(item);
        ci.setQuantity(1);
        cartDao.addCartItem(ci, USER);
        check("C2 加入购物车并回读", cartDao.loadCartItems(cart.getId()).size() == 1);
        cartDao.addCartItem(ci, USER);
        check("C3 重复加购累加数量", cartDao.loadCartItems(cart.getId()).get(0).getQuantity() == 2,
                "qty=" + cartDao.loadCartItems(cart.getId()).get(0).getQuantity());

        // ---------- D. 下单 ----------
        Order order = new Order();
        order.setUserId(USER);
        order.setOrderName("张三");
        order.setOrderAddress("湖南省长沙市岳麓区中南大学");
        order.setOrderTel("13800000000");
        order.setOrderDate(new java.sql.Timestamp(System.currentTimeMillis()));
        order.setCart(cartDao.getCartByUserName(USER)); // 下单明细来自购物车
        List<OrderItem> ois = new ArrayList<OrderItem>();
        OrderItem oi = new OrderItem();
        oi.setItemId("EST-1");
        oi.setQuantity(2);
        oi.setPrice(new BigDecimal("16.50"));
        ois.add(oi);
        order.setOrderItems(ois);

        OrderDaoImpl orderDao = new OrderDaoImpl();
        orderDao.saveOrder(order, USER);
        List<Order> orders = orderDao.getAllOrders(USER);
        check("D1 订单写入并回读", orders.size() == 1, "size=" + orders.size());
        int orderId = orders.get(0).getOrderId();
        check("D2 订单明细回读", orderDao.findOrderItemsByOrderId(orderId, USER).size() == 1);
        check("D3 下单后购物车已清空", cartDao.getCartByUserName(USER) == null);

        // ---------- E. 连接池状态不残留 ----------
        DBUtil.releaseThreadConnection();
        Connection fresh = ConnectionPool.getInstance().getConnection();
        check("E1 归还后 autoCommit 已重置为 true", fresh.getAutoCommit());
        fresh.close();
        DBUtil.releaseThreadConnection();

        // ---------- F. 日志 ----------
        new LogDaoImpl().loginLog(USER, "集成测试登录");
        Object logs = scalar("SELECT COUNT(*) FROM user_log WHERE user_id = '" + USER + "'");
        check("F1 登录日志落库", ((Number) logs).intValue() >= 1, "count=" + logs);

        // ---------- G. 事务模板回滚 ----------
        DBUtil.releaseThreadConnection();
        final int before = orderDao.getAllOrders(USER).size();
        boolean thrown = false;
        try {
            TransactionManager.execute(new TransactionManager.TransactionCallback<Object>() {
                public Object doInTransaction() throws java.sql.SQLException {
                    Connection conn = DBUtil.getConnection();
                    conn.createStatement().executeUpdate(
                            "INSERT INTO orders (userId, orderName, orderDate, orderAddress, orderTel) "
                                    + "VALUES ('" + USER + "', 'SHOULD_ROLLBACK', NOW(), 'x', 'x')");
                    throw new RuntimeException("intentional");
                }
            });
        } catch (RuntimeException e) {
            thrown = true;
        }
        check("G1 事务内异常抛出", thrown);
        check("G2 事务整体回滚", orderDao.getAllOrders(USER).size() == before,
                "before=" + before + ", after=" + orderDao.getAllOrders(USER).size());

        // ---------- H. 池健康 ----------
        DBUtil.releaseThreadConnection();
        String st = ConnectionPool.getInstance().stats();
        check("H1 全部连接已归还", st.contains("inUse=0"), st);

        System.out.println("\n" + st);
        System.out.println("总计: " + pass + " 通过, " + fail + " 失败");
        ConnectionPool.getInstance().shutdown();
        if (fail > 0) System.exit(1);
    }
}
