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

@WebFilter(filterName = "ItemFilter" ,urlPatterns = {"/item"})
public class ItemFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest  req =(HttpServletRequest) servletRequest;
        HttpServletResponse  resp =(HttpServletResponse) servletResponse;

        HttpSession session = req.getSession();
        String itemId = req.getParameter("itemId");
        //String username = (String) session.getAttribute("username");
        String username = req.getParameter("username");
        System.out.println("username:"+username);
        System.out.println("你要查看的具体商品为"+itemId);

        LogService logService =new LogService();
        CatalogService catalogService = new CatalogService();

        Item item = catalogService.getItem(itemId);

        session.setAttribute("item",item);

        if (username!=null && !username.equals("")) {
            int supplier = item.getSupplierId();
            Product product = catalogService.getProduct(item.getProductId());
            String productName = product.getName();
            System.out.println("用户浏览");
            logService.browseLog(username, itemId, username + "浏览了由" + supplier + "提供的" + productName);
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
