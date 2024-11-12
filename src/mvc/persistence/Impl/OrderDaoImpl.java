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
    private static final String INSERT_ORDER_ITEM_SQL = "INSERT INTO order_items (orderId, itemId, quantity, price,username) VALUES (?,?, ?, ?, ?)";
    private static final String CLEAR_CART_SQL = "DELETE FROM cart WHERE username = ?";
    private static final String SELECT_ALL_ORDERS_SQL = "SELECT * FROM orders WHERE userId = ?";
    private static final String FIND_ORDER_BY_ID_SQL = "SELECT * FROM orders WHERE orderId = ?";
    private static final String FIND_ORDER_ITEMS_BY_ORDER_ID_SQL = "SELECT * FROM order_items WHERE orderId  = ? AND username = ?";

    @Override
    public void saveOrder(Order order,String username) {
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
                    insertOrderItemStmt.setString(5,username);
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
    public List<Order> getAllOrders(String username) {

        List<Order> orderList =new ArrayList<>();

        try {
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ORDERS_SQL);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

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
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    @Override
    public List<OrderItem> findOrderItemsByOrderId(int orderId,String username) {
        List<OrderItem> orderItems = new ArrayList<>();

            try  {
                Connection connection = DBUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ORDER_ITEMS_BY_ORDER_ID_SQL);
                preparedStatement.setInt(1,orderId);
                preparedStatement.setString(2, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItemId(resultSet.getString("itemId"));
                    orderItem.setQuantity(resultSet.getInt("quantity"));
                    orderItem.setPrice(resultSet.getBigDecimal("price"));
                    orderItems.add(orderItem);
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderItems;

}
}
