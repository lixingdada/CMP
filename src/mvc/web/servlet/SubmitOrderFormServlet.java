package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.domain.Order;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.persistence.Impl.OrderDaoImpl;
import mvc.persistence.LogDao;
import mvc.persistence.OrderDao;
import mvc.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class SubmitOrderFormServlet extends HttpServlet {

    private OrderDao orderDao = new OrderDaoImpl();
    private CartDao cartDao = new CartDaoImpl();
    private LogService logService = new LogService();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        String username = req.getParameter("username");

        String orderName = req.getParameter("orderName");
        String orderTel = req.getParameter("orderTel");
        String orderAddress = req.getParameter("orderAddress");
        System.out.println("订单数据111："+orderName+orderTel+orderAddress);

        Cart cart ;
        try {
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Order newOrder = new Order();
        newOrder.setUserId(username);

        newOrder.setOrderName(orderName);
        newOrder.setOrderTel(orderTel);
        newOrder.setOrderAddress(orderAddress);
        newOrder.setCart(cart);
        // 将订单添加到订单服务中
        orderDao.saveOrder(newOrder,username);

        //日志
        String itemInfo = "";
        List<CartItem> items = cart.getCartItemList();
        int flag= 0;
        for(CartItem item:items){
            itemInfo+=item.getItem().getItemId();
            flag++;
            if(flag<items.size()){
                itemInfo+="、";
            }
        }
        logService.generateOrders(username,itemInfo,username+"生成了包含"+itemInfo+"的订单！");

        // 清空购物车
        session.removeAttribute("cart");
        //cartDao.deleteCartAndItemsByUserName(username);

        session.setAttribute("username",username);

        req.getRequestDispatcher("/WEB-INF/jsp/order/okorder.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doPost(req, resp);
    }
}
