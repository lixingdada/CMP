package mvc.web.servlet.filter;

import mvc.domain.Item;
import mvc.domain.Product;
import mvc.service.CatalogService;
import mvc.service.LogService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AddItemToCartFilter" ,urlPatterns = {"/addItemToCart"})
public class AddItemToCartFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req =(HttpServletRequest) servletRequest;
        HttpServletResponse resp =(HttpServletResponse) servletResponse;

        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String workingItemId = req.getParameter("itemId");

        CatalogService catalogServiceTemp = new CatalogService();
        LogService logService =new LogService();

        //日志
        if(username!=null&&!username.equals("")){
            Item item = catalogServiceTemp.getItem(workingItemId);
            int supplier = item.getSupplierId();
            Product product = catalogServiceTemp.getProduct(item.getProductId());
            String productName = product.getName();
            System.out.println("加入购物车");
            logService.addCart(username,workingItemId,username+"将由"+ supplier + "提供的" + productName+"加入了购物车！");
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
