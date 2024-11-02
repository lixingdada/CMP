package web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterFormServlet", urlPatterns = {"/jsp/registerForm"})
public class RegisterFormServlet extends HttpServlet {
    private static final String REGISTER_FORM = "/WEB-INF/jsp/register.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(REGISTER_FORM).forward(req, resp);
    }
}
