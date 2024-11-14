package mvc.persistence;

import mvc.domain.Cart;
import mvc.domain.CartItem;

import java.util.List;

public interface CartDao {

    Cart getCartByUserName(String userName) throws Exception;

    void addCartItem(CartItem item, String userName) throws Exception;

    void updateCartItemQuantity(String cartItemId, int quantity,String userName) throws Exception;

    void removeCartItem(String cartItemId,String userName) throws Exception;

    void createCartForUser(String userName) throws Exception;

    List<CartItem> loadCartItems(int cartId) throws Exception;

    void deleteCartAndItemsByUserName(String userName);
}
