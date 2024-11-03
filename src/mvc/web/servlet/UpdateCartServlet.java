package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;

public class UpdateCartServlet extends HttpServlet {

    private static final String CART_FORM="/WEB-INF/jsp/cart/cart.jsp";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session=req.getSession();
        Cart cart=(Cart)session.getAttribute("cart");
        if(cart!=null) {
            Iterator<CartItem> cartItems = cart.getAllCartItems();
            while (cartItems.hasNext()) {
                CartItem cartItem = (CartItem) cartItems.next();
                String itemId = cartItem.getItem().getItemId();

                int quantity = cartItem.getQuantity();

                cart.setQuantityByItemId(itemId, quantity);
                if (quantity < 1) {
                    cartItems.remove();
                }
            }
        }
        req.getRequestDispatcher(CART_FORM).forward(req,resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doPost(req, resp);
    }
}
