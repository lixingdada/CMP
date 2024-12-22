package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.domain.Item;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(name = "RemoveCartItemServlet", value = "/removeCartItem")
public class RemoveCartItemServlet extends HttpServlet {

    private static final String CART_FORM = "/WEB-INF/jsp/cart/cart.jsp";
    private static final String ERROR_FORM = "/WEB-INF/jsp/common/error.jsp";
    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session=req.getSession();
        String username = req.getParameter("username");
        session.setAttribute("username",username);

        System.out.println(username);

        if (username == null) {
            resp.sendRedirect("loginForm");
            return;
        }

        Cart cart = null;
        try {
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String workingItemId = req.getParameter("workingItemId");
        CartItem cartItem=new CartItem();
        cartItem.item = cart.removeItemById(workingItemId);

        if (cartItem.item == null) {
            session.setAttribute("errorMsg", "Attempted to remove null CartItem from Cart.");
            req.getRequestDispatcher(ERROR_FORM).forward(req, resp);
        } else {
            try {
                cartDao.removeCartItem(cartItem.item.getItemId(),username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                cart = cartDao.getCartByUserName(username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            session.setAttribute("cart", cart);
            req.getRequestDispatcher(CART_FORM).forward(req, resp);
        }
    }
}
