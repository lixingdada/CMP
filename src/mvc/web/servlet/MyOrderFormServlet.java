package mvc.web.servlet;

import mvc.domain.Order;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyOrderFormServlet extends HttpServlet {
    private static final String MYORDER_FORM="/WEB-INF/jsp/order/myorder.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

         // 获取订单列表
            List<Order> orderList = (List<Order>) req.getSession().getAttribute("myOrder");
            if (orderList == null) {
                orderList = new ArrayList<>();
                req.getSession().setAttribute("myOrder", orderList);
            }

            // 将订单列表放入请求属性
            req.setAttribute("myOrder", orderList);
        req.getRequestDispatcher(MYORDER_FORM).forward(req,resp);
    }
}
