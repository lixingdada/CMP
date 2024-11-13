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
import java.util.Objects;

public class CartFormServlet extends HttpServlet {
    private static final String CART_FORM = "/WEB-INF/jsp/cart/cart.jsp";
    private static final String LOGIN_FORM = "/WEB-INF/jsp/login.jsp";
    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取当前用户的会话
        HttpSession session=req.getSession();

        String username = req.getParameter("username");

        System.out.println("mingzi:"+username+username.length());

        // 检查用户是否已登录
        if (username.isEmpty()) {
            // 用户未登录，重定向到登录页面
            resp.sendRedirect(req.getContextPath() + "/loginForm");
            return;
        }

        Cart cart ;
        try {
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        session.setAttribute("username",username);
        session.setAttribute("cart", cart);

        // 用户已登录，正常显示购物车页面
        req.getRequestDispatcher(CART_FORM).forward(req, resp);
    }
}
