package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.Order;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.persistence.Impl.OrderDaoImpl;
import mvc.persistence.OrderDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class OrderFormServlet extends HttpServlet {
    //private OrderDao orderDao = new OrderDaoImpl();
    private static final String ORDER_FORM="/WEB-INF/jsp/order/order.jsp";
    private CartDao cartDao = new CartDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session=req.getSession();
        String username = req.getParameter("username");

        // 获取用户输入的数据
        String orderName = req.getParameter("orderName");
        String orderTel = req.getParameter("orderTel");
        String orderAddress = req.getParameter("orderAddress");

        Cart cart ;
        try {
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 创建订单对象
        Order newOrder = new Order();
        newOrder.setUserId(username);

        newOrder.setOrderName(orderName);
        newOrder.setOrderTel(orderTel);
        newOrder.setOrderAddress(orderAddress);
        newOrder.setCart(cart);
        System.out.println("订单数据："+orderName+orderTel+orderAddress);

        session.setAttribute("username",username);
        session.setAttribute("order",newOrder);

        req.getRequestDispatcher(ORDER_FORM).forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        doGet(req, resp);
    }
}
