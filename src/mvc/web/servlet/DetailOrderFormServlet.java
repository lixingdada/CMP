package mvc.web.servlet;

import mvc.domain.Order;
import mvc.domain.OrderItem;
import mvc.persistence.Impl.OrderDaoImpl;
import mvc.persistence.OrderDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class DetailOrderFormServlet extends HttpServlet {
    private static final String LORDERDETAI_FORM="/WEB-INF/jsp/order/orderDetail.jsp";
    private OrderDao orderDao = new OrderDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderIdStr = req.getParameter("orderId");

        HttpSession session=req.getSession();
        String username = req.getParameter("username");
        session.setAttribute("username",username);


        int orderId = Integer.parseInt(orderIdStr);

        // 获取订单项信息
        List<OrderItem> orderItems = orderDao.findOrderItemsByOrderId(orderId,username);

        req.setAttribute("orderItems", orderItems);
        req.getRequestDispatcher(LORDERDETAI_FORM).forward(req, resp);
    }
}
