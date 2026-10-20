package mvc.web.servlet;

import com.alibaba.fastjson2.JSON;
import mvc.domain.Item;
import mvc.domain.Product;
import mvc.service.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MainServlet" , urlPatterns = {"/main"})
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session=req.getSession();
        String username = req.getParameter("username");
        session.setAttribute("username",username);

        CatalogService catalogService = new CatalogService();
        List<Item> list = catalogService.getItemsList();
        session.setAttribute("allItems",list);

        req.getRequestDispatcher("mainForm").forward(req,resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        String search = req.getParameter("information");
//        req.setAttribute("information",search);
//
//        //System.out.println("搜索信息："+search);
//        CatalogService catalogService = new CatalogService();
//        List<Item> itemList = catalogService.getItemListBySearch(search);
//
//        HttpSession session = req.getSession();
//
//        String username = req.getParameter("username");
//        session.setAttribute("username",username);
//
//        session.setAttribute("itemList",itemList);
//        session.setAttribute("msg","null");
//
//        req.getRequestDispatcher("category").forward(req,resp);

//        自动补全

//        自动补全异步请求
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        CatalogService catalogService = new CatalogService();
        String search = req.getParameter("information");
        List<Product> productList = catalogService.getSearchProductList(search);
        List<String> autoComplete = new ArrayList<>();
        for(int i = 0; i < productList.size(); i++){
            autoComplete.add(productList.get(i).getName());
        }

        String json = JSON.toJSONString(autoComplete);
        out.print(json);
        out.close();

    }
}
