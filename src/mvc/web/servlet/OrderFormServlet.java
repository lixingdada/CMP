package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.domain.Order;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.persistence.Impl.OrderDaoImpl;
import mvc.persistence.OrderDao;
import mvc.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "OrderFormServlet", value = "/orderForm")
public class OrderFormServlet extends HttpServlet {
    private static final String OK_ORDER = "/WEB-INF/jsp/order/okorder.jsp";
    private OrderDao orderDao = new OrderDaoImpl();
    private CartDao cartDao = new CartDaoImpl();
    private LogService logService = new LogService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("成功1");
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String orderName = req.getParameter("orderName");
        String orderTel = req.getParameter("orderTel");
        String orderAddress = req.getParameter("orderAddress");

        System.out.println("orderForm+username:"+username);
        try {
            Cart cart = cartDao.getCartByUserName(username);
            Order newOrder = new Order();

            newOrder.setUserId(username);
            newOrder.setOrderName(orderName);
            newOrder.setOrderTel(orderTel);
            newOrder.setOrderAddress(orderAddress);
            newOrder.setCart(cart);

            // 将订单添加到订单服务中
            orderDao.saveOrder(newOrder,username);

//            //日志
//            String itemInfo = "";
//            List<CartItem> items = cart.getCartItemList();
//            int flag= 0;
//            for(CartItem item:items){
//                itemInfo+=item.getItem().getItemId();
//                flag++;
//                if(flag<items.size()){
//                    itemInfo+="、";
//                }
//            }
//            logService.generateOrders(username,itemInfo,username+"生成了包含"+itemInfo+"的订单！");

            HttpSession session = req.getSession();
            session.setAttribute("username",username);
            req.getRequestDispatcher(OK_ORDER).forward(req, resp);

            System.out.println("成功2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        doPost(req, resp);
    }
}
