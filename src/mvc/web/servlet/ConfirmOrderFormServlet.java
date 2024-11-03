package mvc.web.servlet;

import mvc.domain.Cart;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ConfirmOrderFormServlet extends HttpServlet {
    private static final String CONFIRMORDER_FORM="/WEB-INF/jsp/order/confirmorder.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher(CONFIRMORDER_FORM).forward(req,resp);
    }

}
