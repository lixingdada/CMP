package mvc.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class OrderFormServlet extends HttpServlet {

    private static final String ORDER_FORM="/WEB-INF/jsp/order/order.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session=req.getSession();
        String username = req.getParameter("username");
        session.setAttribute("username",username);
        req.getRequestDispatcher(ORDER_FORM).forward(req,resp);
    }
}
