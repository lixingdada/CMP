package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.domain.Item;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AddItemToCartServlet extends HttpServlet {

    private static final String CART_FORM = "/WEB-INF/jsp/cart/cart.jsp";
    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String workingItemId = req.getParameter("workingItemId");
        HttpSession session = req.getSession();
        String username= (String) session.getAttribute("username");

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
        if (cart == null) {
            try {
                cartDao.createCartForUser(username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                cart = cartDao.getCartByUserName(username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        CatalogService catalogService = new CatalogService();
        CartItem cartItem = new CartItem();
        cartItem.item = catalogService.getItem(workingItemId);

        if (cart.containsItemId(workingItemId)) {
            cart.incrementQuantityByItemId(workingItemId);
        } else {
            cart.addItem(cartItem.item);
            try {
                cartDao.addCartItem(cartItem, cart.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        session.setAttribute("cart", cart);
        req.getRequestDispatcher(CART_FORM).forward(req, resp);
    }
}
