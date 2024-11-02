
/*跳转到展示商品中类的servlet*/

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

@WebServlet(name = "CategoryFormServlet", urlPatterns = {"/categoryForm"})
public class CategoryFormServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/jsp/catalog/Category.jsp").forward(req,resp);
    }
}
