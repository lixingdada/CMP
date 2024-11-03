package mvc.web.servlet;

import mvc.domain.User;
import mvc.service.RegisterService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/registerForm"})
public class RegisterServlet extends HttpServlet {
    private RegisterService registerService;

    @Override
    public void init() throws ServletException {
        registerService = new RegisterService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        if (registerService.register(user)) {
            // 注册成功，重定向到登录页面
            resp.sendRedirect(req.getContextPath() + "/jsp/LoginForm");
        } else {
            // 注册失败，提示错误信息并返回注册页面
            req.setAttribute("errorMessage", "注册失败，请重试");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        }
    }
}
