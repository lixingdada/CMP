package mvc.web.servlet;

import mvc.domain.Order;
import mvc.persistence.Impl.OrderDaoImpl;
import mvc.persistence.OrderDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyOrderFormServlet extends HttpServlet {
    private static final String MYORDER_FORM = "/WEB-INF/jsp/order/myorder.jsp";
    private OrderDao orderDao = new OrderDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        session.setAttribute("username", username);

        // 从数据库中获取特定用户名的订单列表
        List<Order> orderList = orderDao.getAllOrders(username);

        // 将订单列表放入请求属性
        req.setAttribute("myOrder", orderList);

        // 请求转发到 JSP 页面
        req.getRequestDispatcher(MYORDER_FORM).forward(req, resp);
    }
}
