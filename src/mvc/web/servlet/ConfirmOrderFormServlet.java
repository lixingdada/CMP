package mvc.web.servlet;

import mvc.domain.Cart;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ConfirmOrderFormServlet extends HttpServlet {
    private static final String CONFIRMORDER_FORM="/WEB-INF/jsp/order/confirmorder.jsp";
    private static final String EMPTY_CART_PAGE = "/WEB-INF/jsp/cart/emptycart.jsp"; // 提示页面路径

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //req.getRequestDispatcher(CONFIRMORDER_FORM).forward(req, resp);

        HttpSession session = req.getSession();
        if (session != null && session.getAttribute("cart") != null) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart.getNumberOfItems() == 0) {

                req.getRequestDispatcher(EMPTY_CART_PAGE).forward(req, resp); // 转发到提示页面
            }else{

                req.getRequestDispatcher(CONFIRMORDER_FORM).forward(req, resp);
            }
        } else {
            req.getRequestDispatcher(EMPTY_CART_PAGE).forward(req, resp); // 转发到提示页面
        }

    }

}
