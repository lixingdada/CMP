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
        Iterator<CartItem> cartItems = cart.getAllCartItems();

 /*       等其它部分完善
        while (cartItems.hasNext()) {
            CartItem cartItem = (CartItem) cartItems.next();
            String itemId = cartItem.getItem().getItemId();
            try {
                String quantityString=req.getParameter(itemId);

                int quantity=Integer.parseInt(quantityString);

                cart.setQuantityByItemId(itemId, quantity);
                if (quantity < 1) {
                    cartItems.remove();
                }
            } catch (Exception e) {
                //ignore parse exceptions on purpose
            }
        }

  */

        req.getRequestDispatcher(CART_FORM).forward(req,resp);

    }
}
