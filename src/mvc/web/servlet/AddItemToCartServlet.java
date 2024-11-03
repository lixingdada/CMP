package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.Item;
import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AddItemToCartServlet extends HttpServlet {

   private static final String CART_FORM="/WEB-INF/jsp/cart/cart.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //要有 <a href="addItemToCart?workingItemId=${cartItem.item.itemId}" class="Button">加入购物车</a>

        String workingItemId = req.getParameter("workingItemId");

        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null) {
            cart = new Cart();
        }

        if (cart.containsItemId(workingItemId)) {
            cart.incrementQuantityByItemId(workingItemId);
        } else {
            CatalogService catalogService=new CatalogService();
            Item item = catalogService.getItem(workingItemId);
            cart.addItem(item);
        }

        session.setAttribute("cart",cart);
        req.getRequestDispatcher(CART_FORM).forward(req,resp);
    }
}
