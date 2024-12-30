package mvc.web.servlet;

import mvc.domain.Address;
import mvc.domain.Cart;
import mvc.domain.User;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.persistence.Impl.ReceiverDaoImpl;
import mvc.persistence.ReceiverDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ConfirmOrderFormServlet", value = "/confirmOrderForm")
public class ConfirmOrderFormServlet extends HttpServlet {
    private static final String CONFIRMORDER_FORM="/WEB-INF/jsp/order/confirmorder.jsp";
    private static final String EMPTY_CART_PAGE = "/WEB-INF/jsp/cart/emptycart.jsp"; // 提示页面路径
    private CartDao cartDao = new CartDaoImpl();
    private ReceiverDao receiverDao = new ReceiverDaoImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String username = req.getParameter("username");

        System.out.println(username+" makabama");
        try {
            Cart cart = cartDao.getCartByUserName(username);
            if(cart!=null) {
                if (cart.getNumberOfItems() == 0) {
                    session.setAttribute("username",username);
                    req.getRequestDispatcher(EMPTY_CART_PAGE).forward(req, resp); // 转发到提示页面
                }else{
                    session.setAttribute("username",username);
                    session.setAttribute("cart",cart);
                    List<Address> receivers = receiverDao.getReceiversByUserId(username);
                    session.setAttribute("receivers", receivers);
                    req.getRequestDispatcher(CONFIRMORDER_FORM).forward(req, resp);
                }
            }
            else{
                session.setAttribute("username",username);
                req.getRequestDispatcher(EMPTY_CART_PAGE).forward(req, resp); // 转发到提示页面
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
