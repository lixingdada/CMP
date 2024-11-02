package mvc.web.servlet;

import mvc.domain.Item;
import mvc.domain.Product;
import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ProductFormServlet extends HttpServlet {
    private static  final  String PRODUCT_FORM = "/WEB-INF/jsp/catalog/product.jsp";

    private CatalogService catalogService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productId=req.getParameter("productId");
        Product product=catalogService.getProduct(productId);
        List<Item> itemList=catalogService.getItemListByProduct(productId);
        HttpSession session=req.getSession();
        session.setAttribute("product",product);
        session.setAttribute("itemList",itemList);
        req.getRequestDispatcher(PRODUCT_FORM).forward(req,resp);
    }
}
