package mvc.web.servlet.filter;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.service.LogService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebFilter(filterName = "PayOrderFilter" ,urlPatterns = {"/orderForm"})
public class PayOrderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req =(HttpServletRequest) servletRequest;
        HttpServletResponse resp =(HttpServletResponse) servletResponse;

        String username = req.getParameter("username");
        String orderName = req.getParameter("orderName");
        String orderTel = req.getParameter("orderTel");
        String orderAddress = req.getParameter("orderAddress");

        CartDao cartDao = new CartDaoImpl();
        LogService logService = new LogService();
        Cart cart = null;
        try {
             cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String itemInfo = "";
        List<CartItem> items = cart.getCartItemList();
        int flag= 0;
        for(CartItem item:items){
            itemInfo += item.getItem().getItemId();
            flag++;
            if(flag<items.size()){
                itemInfo+="、";
            }
        }
        logService.payOrder(username,itemInfo,username+"支付了包含"+itemInfo+"的订单！");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
