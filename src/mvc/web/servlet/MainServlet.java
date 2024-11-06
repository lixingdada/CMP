package mvc.web.servlet;

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

@WebServlet(name = "MainServlet" , urlPatterns = {"/main"})
public class MainServlet extends HttpServlet {
     @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         req.getRequestDispatcher("mainForm").forward(req,resp);
     }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String search = req.getParameter("information");
        req.setAttribute("information",search);

        //System.out.println("搜索信息："+search);
        CatalogService catalogService = new CatalogService();
        List<Product> productList = catalogService.getSearchProductList(search);

        HttpSession session = req.getSession();
        session.setAttribute("productList",productList);
        session.setAttribute("msg","null");

        req.getRequestDispatcher("category").forward(req,resp);
    }
}
