//package mvc.web.servlet;
//
//import mvc.domain.Cart;
//import mvc.domain.Order;
//import mvc.persistence.Impl.OrderDaoImpl;
//import mvc.persistence.OrderDao;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//
//public class SubmitOrderFormServlet extends HttpServlet {
//    private OrderDaoImpl orderDaoImpl=new OrderDaoImpl();
//
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        HttpSession session=req.getSession();
//        Cart cart=(Cart)session.getAttribute("cart");
//
//        // 获取用户输入的数据
//        String orderName = req.getParameter("orderName");
//        String orderTel = req.getParameter("orderTel");
//        String orderAddress = req.getParameter("orderAddress");
//
//        // 创建订单对象
//        Order newOrder = new Order();
//
//        newOrder.setOrderName(orderName);
//        newOrder.setOrderTel(orderTel);
//        newOrder.setOrderAddress(orderAddress);
//        newOrder.setCart(cart);
//
//        // 将订单添加到订单服务中
//        orderDaoImpl.saveOrder(newOrder);
//
//        // 清空购物车
//        session.removeAttribute("cart");
//
//        req.getRequestDispatcher("/WEB-INF/jsp/catalog/main.jsp").forward(req, resp);
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        doPost(req, resp);
//    }
//}
