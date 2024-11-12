package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;

public class UpdateCartServlet extends HttpServlet {

    private static final String CART_FORM = "/WEB-INF/jsp/cart/cart.jsp";
    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        
        String username = req.getParameter("username");
        session.setAttribute("username",username);


        Cart cart = null;
        try {
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (cart != null) {
            Iterator<CartItem> cartItems = cart.getAllCartItems();
            while (cartItems.hasNext()) {
                CartItem cartItem = cartItems.next();
                String itemId = cartItem.getItem().getItemId();
                int quantity = Integer.parseInt(req.getParameter(itemId));
                cart.setQuantityByItemId(itemId, quantity);
                try {
                    cartDao.updateCartItemQuantity(cartItem.item.getItemId(), quantity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (quantity < 1) {
                    cartItems.remove();
                    try {
                        cartDao.removeCartItem(cartItem.item.getItemId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        req.getRequestDispatcher(CART_FORM).forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
