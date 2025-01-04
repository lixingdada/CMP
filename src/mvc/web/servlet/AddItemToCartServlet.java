package mvc.web.servlet;

import mvc.domain.Cart;
import mvc.domain.CartItem;
import mvc.domain.Item;
import mvc.domain.Product;
import mvc.persistence.CartDao;
import mvc.persistence.Impl.CartDaoImpl;
import mvc.service.CatalogService;
import mvc.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AddItemToCartServlet extends HttpServlet {

    private static final String CART_FORM = "/WEB-INF/jsp/cart/cart.jsp";
    private CartDao cartDao = new CartDaoImpl();
    private LogService logService = new LogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        String workingItemId = req.getParameter("itemId");
        String username = req.getParameter("username");

        System.out.println("username"+username);
        session.setAttribute("username",username);

        CatalogService catalogServiceTemp = new CatalogService();
        CartItem cartItem = new CartItem();
        cartItem.item = catalogServiceTemp.getItem(workingItemId);

        if (username == null||username.equals("")) {
            resp.sendRedirect("loginForm");
            return;
        }

        Cart cart ;
        try {
            cart = cartDao.getCartByUserName(username);    //尝试数据库中获取cart
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (cart == null) {
            try {
                cartDao.createCartForUser(username);   //数据库中创建cart
                //cart = cartDao.getCartByUserName(username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            cartDao.addCartItem(cartItem,username);  //数据库中对应cart_item
            cart = cartDao.getCartByUserName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        session.setAttribute("cart", cart);

        //日志
        if(username!=null&&!username.equals("")){
            Item item = catalogServiceTemp.getItem(workingItemId);
            int supplier = item.getSupplierId();
            Product product = catalogServiceTemp.getProduct(item.getProductId());
            String productName = product.getName();
            System.out.println("加入购物车");
            logService.addCart(username,workingItemId,username+"将由"+ supplier + "提供的" + productName+"加入了购物车！");
        }
        CatalogService catalogService = new CatalogService();

        session.setAttribute("catalogService", catalogService);
        req.getRequestDispatcher(CART_FORM).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }
}
