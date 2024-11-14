package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.service.CatalogService;


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

        System.out.println(username);

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
                String itemId = String.valueOf(cartItem.item.getItemId());

                String quantityStr = req.getParameter(itemId);
                int quantity = Integer.parseInt(quantityStr != null ? quantityStr : "0");

                try {
                    cartDao.updateCartItemQuantity(itemId, quantity,username);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (quantity < 1) {
                    cartItems.remove();
                    try {
                        cartDao.removeCartItem(cartItem.item.getItemId(),username);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        try {
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.setAttribute("cart", cart);
        req.getRequestDispatcher(CART_FORM).forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
