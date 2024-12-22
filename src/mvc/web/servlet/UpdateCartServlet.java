package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Iterator;


@WebServlet(name = "UpdateCartServlet", value = "/updateCart")
public class UpdateCartServlet extends HttpServlet {

     private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        System.out.println("此时的username:" + username);

        String itemId = req.getParameter("itemId");
        System.out.println("此时的itemId:" + itemId);

        String quantityStr = req.getParameter("quantity");
        System.out.println("此时的quantityStr:" + quantityStr);
        int quantity = 0;

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        try {
            quantity = Integer.parseInt(quantityStr);
            System.out.println("此时的quantity:" + quantity);

            Cart cart = cartDao.getCartByUserName(username);
            if (cart != null) {
                Iterator<CartItem> cartItems = cart.getAllCartItems();
                while (cartItems.hasNext()) {
                    CartItem cartItem = cartItems.next();

                    if (cartItem.item.getItemId().toString().equals(itemId)) {

                        if(quantity == 0){
                            cartDao.removeCartItem(itemId, username);
                            //先0后数的逻辑要加
                        }else{
                            cartDao.updateCartItemQuantity(itemId, quantity, username);
                        }
                        BigDecimal newTotal = cartDao.getCartByUserName(username).getSubTotal();
                        out.write(String.valueOf(newTotal)); //返回新的总价
                        break;
                    }
                }
            }
        }
            catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
