package mvc.web.servlet;

import mvc.domain.Item;
import mvc.domain.Product;
import mvc.service.CatalogService;
import mvc.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ItemServlet",urlPatterns = {"/item"})
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
         String itemId = req.getParameter("itemId");
         String username = (String) session.getAttribute("username");
        System.out.println("你要查看的具体商品为"+itemId);

        LogService logService =new LogService();
        CatalogService catalogService = new CatalogService();

        Item item = catalogService.getItem(itemId);

        session.setAttribute("item",item);

        if (username!=null && !username.equals("")) {
            int supplier = item.getSupplierId();
            Product product = catalogService.getProduct(item.getProductId());
            String productName = product.getName();

            logService.browseLog(username, itemId, username + "浏览了由" + supplier + "提供的" + productName);
        }

        req.getRequestDispatcher("itemForm").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*String itemId = req.getParameter("itemId");
        System.out.println("你要查看的具体商品为"+itemId);
        CatalogService catalogService = new CatalogService();
        Item item = catalogService.getItem(itemId);
        req.setAttribute("item",item);*/

        req.getRequestDispatcher("itemForm").forward(req,resp);
    }
}
