package mvc.web.servlet;


import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ProductServlet",urlPatterns = {"/product"})
public class ProductServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("productForm").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();


        if (req.getParameter("productId")!=null&&!req.getParameter("productId").equals("")){

            System.out.println("here");
            /*从提交的表单中获取这些值*/
            String productId = req.getParameter("productId");
            String productName = req.getParameter("productName");
            //req.setAttribute("productName",productName);

       /* System.out.println("productId"+productId);
        System.out.println("productName"+req.getParameter("productName"));*/

            //String productName = req.getParameter("productName");


            /*调用业务逻辑*/
            CatalogService catalogService = new CatalogService();
            session.setAttribute("ItemList",catalogService.getItemList(productId));
            session.setAttribute("productName",productName);
        }



        req.getRequestDispatcher("productForm").forward(req,resp);
    }
}
