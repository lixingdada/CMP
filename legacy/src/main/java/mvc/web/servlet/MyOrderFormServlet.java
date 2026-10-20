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

        if(req.getParameter("username")==null||req.getParameter("username").equals("")){
            req.getRequestDispatcher("loginForm").forward(req, resp);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String username = req.getParameter("username");


        // 从数据库中获取特定用户名的订单列表
        List<Order> orderList = orderDao.getAllOrders(username);

        // 将订单列表放入请求属性
        session.setAttribute("myOrder", orderList);
        session.setAttribute("username", username);

        // 请求转发到 JSP 页面
        req.getRequestDispatcher(MYORDER_FORM).forward(req, resp);
    }
}

