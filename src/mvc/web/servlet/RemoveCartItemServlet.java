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
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Iterator;


@WebServlet(name = "RemoveCartItemServlet", value = "/removeCartItem")
public class RemoveCartItemServlet extends HttpServlet {

    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        System.out.println("此时的username:" + username);

        String itemId = req.getParameter("itemId");
        System.out.println("此时的itemId:" + itemId);

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        try {
            Cart cart = cartDao.getCartByUserName(username);
            if (cart != null) {
                Iterator<CartItem> cartItems = cart.getAllCartItems();
                while (cartItems.hasNext()) {
                    CartItem cartItem = cartItems.next();

                    if (cartItem.item.getItemId().toString().equals(itemId)) {
                        cartDao.removeCartItem(itemId, username);
                        break;
                    }
                }
                BigDecimal newTotal = cartDao.getCartByUserName(username).getSubTotal();
                out.write(String.valueOf(newTotal)); //返回新的总价
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
