package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ConfirmOrderFormServlet extends HttpServlet {
    private static final String CONFIRMORDER_FORM="/WEB-INF/jsp/order/confirmorder.jsp";
    private static final String EMPTY_CART_PAGE = "/WEB-INF/jsp/cart/emptycart.jsp"; // 提示页面路径
    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session=req.getSession();
        String username = req.getParameter("username");

        if (session.getAttribute("cart") != null) {
            Cart cart ;
            try {
                cart = cartDao.getCartByUserName(username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (cart.getNumberOfItems() == 0) {
                session.setAttribute("username",username);
                req.getRequestDispatcher(EMPTY_CART_PAGE).forward(req, resp); // 转发到提示页面
            }else{
                session.setAttribute("username",username);

                System.out.println("username:"+username);

                session.setAttribute("cart",cart);
                req.getRequestDispatcher(CONFIRMORDER_FORM).forward(req, resp);
            }
        } else {
            session.setAttribute("username",username);
            req.getRequestDispatcher(EMPTY_CART_PAGE).forward(req, resp); // 转发到提示页面
        }
    }

}
