package mvc.persistence.Impl;

import mvc.domain.MyOrder;
import mvc.domain.OrderItem;
import mvc.persistence.OrderDao;
import mvc.domain.Order;
import mvc.domain.CartItem;
import mvc.persistence.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    private static final String INSERT_ORDER_SQL = "INSERT INTO orders (userId, orderName, orderDate, orderAddress, orderTel) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_ORDER_ITEM_SQL = "INSERT INTO order_items (orderId, itemId, quantity, price) VALUES (?, ?, ?, ?)";
    private static final String CLEAR_CART_SQL = "DELETE FROM cart WHERE username = ?";
    private static final String SELECT_ALL_ORDERS_SQL = "SELECT * FROM orders";
    private static final String FIND_ORDER_BY_ID_SQL = "SELECT * FROM orders WHERE orderId = ?";
    private static final String FIND_ORDER_ITEMS_BY_ORDER_ID_SQL = "SELECT * FROM order_items WHERE orderId = ?";

    @Override
    public void saveOrder(Order order) {
        Connection connection = null;
        PreparedStatement insertOrderStmt = null;
        PreparedStatement insertOrderItemStmt = null;
        PreparedStatement clearCartStmt = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false); // 开启事务

            // 插入订单
            insertOrderStmt = connection.prepareStatement(INSERT_ORDER_SQL, Statement.RETURN_GENERATED_KEYS);
            insertOrderStmt.setString(1, order.getUserId());
            insertOrderStmt.setString(2, order.getOrderName());
            insertOrderStmt.setDate(3, new java.sql.Date(order.getOrderDate().getTime()));
            insertOrderStmt.setString(4, order.getOrderAddress());
            insertOrderStmt.setString(5, order.getOrderTel());
            insertOrderStmt.executeUpdate();

            generatedKeys = insertOrderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setOrderId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            // 插入订单项
            if (order.getCart() != null && order.getCart().getCartItemList() != null) {
                insertOrderItemStmt = connection.prepareStatement(INSERT_ORDER_ITEM_SQL);
                for (CartItem cartItem : order.getCart().getCartItemList()) {
                    insertOrderItemStmt.setInt(1, order.getOrderId());
                    insertOrderItemStmt.setString(2, cartItem.item.getItemId());
                    insertOrderItemStmt.setInt(3, cartItem.item.getQuantity());
                    insertOrderItemStmt.setBigDecimal(4, cartItem.total);
                    insertOrderItemStmt.addBatch();
                }
                insertOrderItemStmt.executeBatch();
            }

            // 清空购物车
            clearCartStmt = connection.prepareStatement(CLEAR_CART_SQL);
            clearCartStmt.setString(1, order.getUserId());
            clearCartStmt.executeUpdate();

            connection.commit(); // 提交事务
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // 发生异常时回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (insertOrderStmt != null) {
                try {
                    insertOrderStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (insertOrderItemStmt != null) {
                try {
                    insertOrderItemStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (clearCartStmt != null) {
                try {
                    clearCartStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public MyOrder getAllOrders() {
        MyOrder myOrder = new MyOrder();
        List<Order> orderList = myOrder.getOrderList();

        try {
            Connection connection = DBUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_ORDERS_SQL);

            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getString("userId"));
                order.setOrderName(resultSet.getString("orderName"));
                order.setOrderDate(resultSet.getDate("orderDate"));
                order.setOrderAddress(resultSet.getString("orderAddress"));
                order.setOrderTel(resultSet.getString("orderTel"));
                orderList.add(order);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return myOrder;
    }

    @Override
    public List<OrderItem> findOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_ORDER_ITEMS_BY_ORDER_ID_SQL)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItemId(rs.getString("itemId"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setPrice(rs.getBigDecimal("price"));
                    orderItems.add(orderItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;

}
}
