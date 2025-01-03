package mvc.web.servlet;


import mvc.domain.Item;
import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductServlet",urlPatterns = {"/product"})
public class ProductServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String productId = req.getParameter("productId");
        String productName = req.getParameter("productName");

        //%2B不知道为什么不能解析
        productName = productName.replace("%2B", "+");

        req.setAttribute("productName",productName);

        CatalogService catalogService = new CatalogService();

        HttpSession session = req.getSession();
        session.setAttribute("ItemList",catalogService.getItemList(productId));
        session.setAttribute("information",productName);

        req.getRequestDispatcher("productForm").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        HttpSession session = req.getSession();
//
//
//        if (req.getParameter("productId")!=null&&!req.getParameter("productId").equals("")){
//
//            System.out.println("here");
//            /*从提交的表单中获取这些值*/
//            String productId = req.getParameter("productId");
//            String productName = req.getParameter("productName");
//            //req.setAttribute("productName",productName);
//
//       /* System.out.println("productId"+productId);
//        System.out.println("productName"+req.getParameter("productName"));*/
//
//            //String productName = req.getParameter("productName");
//
//
//            /*调用业务逻辑*/
//            CatalogService catalogService = new CatalogService();
//            session.setAttribute("ItemList",catalogService.getItemList(productId));
//            session.setAttribute("productName",productName);
//        }
//
//
//
//        req.getRequestDispatcher("productForm").forward(req,resp);

        req.setCharacterEncoding("UTF-8");
        String search = req.getParameter("information");


        //System.out.println("搜索信息："+search);
        CatalogService catalogService = new CatalogService();
        List<Item> itemList = catalogService.getItemListBySearch(search);
//        String productName = "";
//        if(itemList != null)
//            productName = catalogService.getProductNameByItemId(itemList.get(0).getItemId());

//        System.out.println("搜索结果："+itemList);
        HttpSession session = req.getSession();

        session.setAttribute("ItemList",itemList);
        session.setAttribute("information",search);
//        req.setAttribute("productName",productName);

//        session.setAttribute("msg","null");

        req.getRequestDispatcher("productForm").forward(req,resp);
    }
}
