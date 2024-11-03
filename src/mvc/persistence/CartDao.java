package mvc.persistence;

import mvc.domain.Cart;
import mvc.domain.CartItem;

import java.util.List;

public interface CartDao {

    Cart getCartByUserId(int userId) throws Exception;

    void addCartItem(CartItem item, int cartId) throws Exception;

    void updateCartItemQuantity(String cartItemId, int quantity) throws Exception;

    void removeCartItem(String cartItemId) throws Exception;

    void createCartForUser(int userId) throws Exception;

    List<CartItem> loadCartItems(int cartId) throws Exception;
}
