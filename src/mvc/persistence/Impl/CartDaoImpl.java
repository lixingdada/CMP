package mvc.persistence.Impl;

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
    private static final String UPDATE_CART_ITEM_QUANTITY = "UPDATE cart_item SET quantity = ? WHERE item_id = ?";
    private static final String DELETE_CART_ITEM = "DELETE FROM cart_item WHERE item_id = ?";
    private static final String CREATE_CART_FOR_USER = "INSERT INTO cart (username) VALUES (?)";
    private static final String SELECT_CART_ITEMS = "SELECT * FROM cart_item WHERE cart_id = ?";

    @Override
    public Cart getCartByUserName(String userName){
            Cart cart = new Cart();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = DBUtil.getConnection();
                preparedStatement = connection.prepareStatement(SELECT_CART_BY_USER_ID);
                preparedStatement.setString(1, userName);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    cart.setId(resultSet.getInt("cart_id"));
                    cart.setUserName(userName);
                }
                DBUtil.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cart.setItems(loadCartItems(cart.getId()));
            return cart;
        }

    public void addCartItem(CartItem cartItem, int cartId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_CART_ITEM);
            preparedStatement.setInt(1, cartId);
            preparedStatement.setString(2, cartItem.item.getItemId());
            preparedStatement.setBigDecimal(3, cartItem.item.getListPrice());
            preparedStatement.setInt(4, cartItem.getQuantity());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateCartItemQuantity(String cartItemId, int quantity) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_CART_ITEM_QUANTITY);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setString(2, cartItemId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void removeCartItem(String cartItemId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_CART_ITEM);
            preparedStatement.setString(1, cartItemId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void createCartForUser(String userName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_CART_FOR_USER);
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch(Exception e){
            e.printStackTrace();
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
                cartItem.item.setItemId(resultSet.getString("item_name"));
                cartItem.item.setListPrice(resultSet.getBigDecimal("item_price"));
                cartItem.setQuantity(resultSet.getInt("quantity"));
                cartItems.add(cartItem);
            }
            DBUtil.close();
        }catch(Exception e){
                e.printStackTrace();
            }
        return cartItems;
    }
}
