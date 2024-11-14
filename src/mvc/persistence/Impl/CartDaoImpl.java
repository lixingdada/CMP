package mvc.persistence.Impl;

import mvc.domain.Item;
import mvc.persistence.CartDao;
import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.persistence.DBUtil;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDaoImpl implements CartDao {

    private static final String SELECT_CART_BY_USER_ID = "SELECT * FROM cart WHERE username = ?";
    private static final String INSERT_CART_ITEM = "INSERT INTO cart_item (cart_id, item_name, item_price, quantity) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_CART_ITEM_QUANTITY_BY_CART_ID = "UPDATE cart_item SET quantity = ? WHERE item_id = ?";
    private static final String DELETE_CART_ITEM = "DELETE FROM cart_item WHERE cart_id = ? AND item_name = ?";

    private static final String CREATE_CART_FOR_USER = "INSERT INTO cart (username) VALUES (?)";
    private static final String SELECT_CART_ITEMS = "SELECT * FROM cart_item WHERE cart_id = ?";
    private static final String UPDATE_CART_ITEMS_QUANTITY="UPDATE cart_item SET quantity = ? WHERE cart_id = ? AND item_name = ?";
    private static final String SELECT_CART_ID_BY_USERNAME="SELECT cart_id FROM cart WHERE username = ?";

    private static final String DELETE_ALL_CART = "DELETE FROM cart WHERE user_name = ?";
    private static final String DELETE_ALL_CART_ITEM = "DELETE FROM cart_item WHERE cart_id =?";


    @Override
    public Cart getCartByUserName(String userName) throws SQLException {
            Cart cart = new Cart();
            Connection connection = null;

            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = DBUtil.getConnection();
                connection.setAutoCommit(false); // 开启事务
                preparedStatement = connection.prepareStatement(SELECT_CART_BY_USER_ID);
                preparedStatement.setString(1, userName);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int cartId = resultSet.getInt("cart_id");
                    cart.setId(cartId);
                    cart.setUserName(userName);

                    List<CartItem> cartItems = loadCartItems(cartId);
                    for (CartItem cartItem : cartItems) {
                        cart.addItem(cartItem.item);
                        System.out.println(cartItem.item.getItemId()+"的数量为"+cartItem.getQuantity());
                        cart.setQuantityByItemId(cartItem.item.getItemId(), cartItem.getQuantity());
                    }

                    connection.commit();
                    System.out.println("数据库创建购物车不为空");
                    return cart;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (connection != null) {
                    try {
                        connection.rollback(); // 回滚事务
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }finally {
                DBUtil.closeResultSet(resultSet); // 关闭 ResultSet
                DBUtil.closePreparedStatement(preparedStatement); // 关闭 PreparedStatement
                DBUtil.closeConnection(connection); // 关闭 Connection
            }
        System.out.println("数据库创建购物车为空");
        return null;
        }

    public void addCartItem(CartItem cartItem, String userName) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement preparedStatement3 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;

        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false); // 开启事务

            preparedStatement1 = connection.prepareStatement(SELECT_CART_BY_USER_ID);
            preparedStatement1.setString(1, userName);
            resultSet1 = preparedStatement1.executeQuery();

            int cartId = -1; // 初始化为一个无效值
            if (resultSet1.next()) { // 确保结果集有数据
                cartId = resultSet1.getInt("cart_id");
            } else {
                throw new SQLException("No cart found for user: " + userName);
            }

            // 检查商品是否已经存在
            preparedStatement2 = connection.prepareStatement(SELECT_CART_ITEMS);
            preparedStatement2.setInt(1, cartId);
            resultSet2 = preparedStatement2.executeQuery();
            boolean exists = false;
            while (resultSet2.next()) {
                //System.out.println("找到了");
                if (resultSet2.getString("item_name").equals(cartItem.item.getItemId())) {
                    exists = true;
                    int currentQuantity = resultSet2.getInt("quantity");

                    int newQuantity = currentQuantity + 1;

                    preparedStatement3 = connection.prepareStatement(UPDATE_CART_ITEM_QUANTITY_BY_CART_ID);

                    preparedStatement3.setInt(1, newQuantity);

                    int itemId = resultSet2.getInt("item_id");
                    preparedStatement3.setInt(2, itemId);

                    System.out.println("Executing: " + preparedStatement3.toString());

                    preparedStatement3.executeUpdate();
                    break;
                }
            }

            if (!exists) {
                //System.out.println("没找到");
                preparedStatement3 = connection.prepareStatement(INSERT_CART_ITEM);
                preparedStatement3.setInt(1, cartId);
                preparedStatement3.setString(2, cartItem.item.getItemId());
                preparedStatement3.setBigDecimal(3, cartItem.item.getListPrice());
                preparedStatement3.setInt(4, 1);
                preparedStatement3.executeUpdate();
            }

            connection.commit();
            DBUtil.close();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            DBUtil.closeResultSet(resultSet2);
            DBUtil.closeResultSet(resultSet1);
            DBUtil.closePreparedStatement(preparedStatement3);
            DBUtil.closePreparedStatement(preparedStatement2);
            DBUtil.closePreparedStatement(preparedStatement1);
            DBUtil.closeConnection(connection);
        }
    }


    @Override
    public void updateCartItemQuantity(String cartItemId, int quantity, String userName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false); // 开启事务

            // 获取 cartId
            int cartId = getCartIdByUsername(userName, connection);

            // 更新商品数量
            preparedStatement = connection.prepareStatement(UPDATE_CART_ITEMS_QUANTITY);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, cartId);
            preparedStatement.setString(3, cartItemId);

            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
    }

    private int getCartIdByUsername(String userName, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(SELECT_CART_ID_BY_USERNAME);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("cart_id");
            } else {
                throw new SQLException("No cart found for user: " + userName);
            }
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    @Override
    public void removeCartItem(String itemName, String userName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false); // 开启事务

            // 获取 cartId
            int cartId = getCartIdByUsername(userName, connection);

            // 删除与 item_name 相关的所有记录
            preparedStatement = connection.prepareStatement(DELETE_CART_ITEM);
            preparedStatement.setInt(1, cartId);
            preparedStatement.setString(2, itemName);
            preparedStatement.executeUpdate();

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
    }

    @Override
    public void createCartForUser(String userName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false); // 开启事务
            preparedStatement = connection.prepareStatement(CREATE_CART_FOR_USER);
            System.out.println(userName);
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();

            connection.commit();

            System.out.println("数据库创建购物车成功");
        } catch(Exception e){
            System.out.println("数据库创建购物车失败");

        if (connection != null) {
            try {
                connection.rollback(); // 发生异常时回滚事务
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        e.printStackTrace();
    } finally {
            DBUtil.closeConnection(connection);
            DBUtil.closePreparedStatement(preparedStatement);
    }


    }

    public List<CartItem> loadCartItems(int cartId){
        List<CartItem> cartItems = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_CART_ITEMS);
            preparedStatement.setInt(1, cartId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setItem(new Item());
                cartItem.item.setItemId(resultSet.getString("item_name"));
                cartItem.item.setListPrice(resultSet.getBigDecimal("item_price"));
                cartItem.setQuantity(resultSet.getInt("quantity"));
                System.out.println(resultSet.getString("item_name")+"是"+resultSet.getInt("quantity"));
                cartItems.add(cartItem);
            }
        }catch(Exception e){
                e.printStackTrace();
            }finally {
            // 确保在 finally 块中关闭资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return cartItems;
    }

    public void deleteCartAndItemsByUserName(String userName) {
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement preparedStatement3 = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false); // 开启事务

            int cartId = getCartIdByUsername(userName, connection);

            preparedStatement1 = connection.prepareStatement(SELECT_CART_ITEMS);
            preparedStatement1.setInt(1, cartId);
            resultSet = preparedStatement1.executeQuery();

            preparedStatement2 = connection.prepareStatement(DELETE_ALL_CART_ITEM);
            while (resultSet.next()) {
                preparedStatement2.setInt(1, cartId);
                preparedStatement2.executeUpdate();
            }

            preparedStatement3 = connection.prepareStatement(DELETE_ALL_CART);
            preparedStatement3.setString(1, userName);
            preparedStatement3.executeUpdate();

        connection.commit();
    } catch (Exception e) {
        if (connection != null) {
            try {
                connection.rollback(); // 回滚事务
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        e.printStackTrace();
    } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement3);
            DBUtil.closePreparedStatement(preparedStatement2);
            DBUtil.closePreparedStatement(preparedStatement1);
            DBUtil.closeConnection(connection);
        }
    }
}
