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
import java.math.BigDecimal;
import java.util.List;

public class DetailOrderFormServlet extends HttpServlet {
    private static final String LORDERDETAI_FORM="/WEB-INF/jsp/order/orderDetail.jsp";
    private OrderDao orderDao = new OrderDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session=req.getSession();
        String username = req.getParameter("username");

        String orderIdStr = req.getParameter("orderId");
        int orderId = Integer.parseInt(orderIdStr);

        // 获取订单项信息
        List<OrderItem> orderItems = orderDao.findOrderItemsByOrderId(orderId,username);

        BigDecimal totalPrice = new BigDecimal(0);

        for(OrderItem orderItem:orderItems) {
            totalPrice = totalPrice.add(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
        }

        session.setAttribute("orderItems", orderItems);
        session.setAttribute("username",username);
        session.setAttribute("totalPrice", totalPrice);
        req.getRequestDispatcher(LORDERDETAI_FORM).forward(req, resp);
    }
}
