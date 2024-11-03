/*跳转到展示商品中类的servlet*/
package mvc.web.servlet;

import mvc.domain.Category;

import mvc.domain.Product;
import mvc.service.CatalogService;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import java.util.List;

public class CategoryFormServlet extends HttpServlet {
    private static  final  String CATEGORY_FORM = "/WEB-INF/jsp/catalog/category.jsp";

    private CatalogService catalogService;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*获取我点进了哪个商品大类*/
        String categoryId=req.getParameter("categoryId");
        catalogService =new CatalogService();
        Category category=catalogService.getCategory(categoryId);
        List<Product> productList=catalogService.getProductListByCategory(categoryId);

        /*存入session中*/
        HttpSession session=req.getSession();
        session.setAttribute("category",category);
        session.setAttribute("productList",productList);

        /*展示*/
        req.getRequestDispatcher(CATEGORY_FORM).forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/jsp/catalog/Category.jsp").forward(req,resp);
    }
}
