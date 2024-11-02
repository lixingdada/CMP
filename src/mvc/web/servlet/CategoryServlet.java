package mvc.web.servlet;

import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CategoryServlet",urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*获取我点进了哪个商品大类*/
        String category =  req.getParameter("category");
        System.out.println(category);

        /*存入session中*/
        HttpSession session = req.getSession();
        session.setAttribute("category",category);


        /*调用业务逻辑，并将商品中类列表存到session中*/
        CatalogService catalogService = new CatalogService();
//        System.out.println("productList在这"+catalogService.getProductList(category));
        session.setAttribute("productList",catalogService.getProductList(category));
        session.setAttribute("msg","flag");

        /*展示*/
        //resp.sendRedirect("categoryForm");
        req.getRequestDispatcher("categoryForm").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("categoryForm").forward(req,resp);
    }
}
