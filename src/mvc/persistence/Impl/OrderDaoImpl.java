//package mvc.persistence.Impl;
//
//import mvc.domain.MyOrder;
//import mvc.domain.Order;
//import mvc.persistence.DBUtil;
//import mvc.persistence.OrderDao;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class OrderDaoImpl implements OrderDao {
//    private static final String sql = "SELECT * FROM orders WHERE userId = ?";
//
//    @Override
//    public void saveOrder(Order order) {
//
//    }
//
//    @Override
//    public MyOrder getAllOrders() {
//            MyOrder orders = new MyOrder();
//            List<Order> orderList = orders.orderList;
//
//            try {
//                Connection connection = DBUtil.getConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                ResultSet resultSet = preparedStatement.executeQuery();
//
//                while (resultSet.next()) {
//                    Order order = new Order();
//                    order.setOrderId(resultSet.getInt("orderId"));
//                    order.setOrderDate(resultSet.getDate("orderDate"));
//                    order.setOrderAddress(resultSet.getString("orderAddress"));
//
//                    orderList.add(order);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                DBUtil.close();
//            }
//
//            return orders;
//    }
//}
