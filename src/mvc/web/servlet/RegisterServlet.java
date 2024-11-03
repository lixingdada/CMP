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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     /*   String username = req.getParameter("newUsername"); // 确保获取到正确的注册用户名
        String password = req.getParameter("newPassword"); // 确保获取到正确的注册密码

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        if (registerService.register(user)) {
            // 注册成功，重定向到登录页面，并携带提示信息
            req.setAttribute("successMessage", "注册成功，请登录");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } else {
            // 注册失败，提示错误信息并返回登录页面
            req.setAttribute("errorMessage", "注册失败，请重试");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }*/
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("newUsername"); // 确保获取到正确的注册用户名
        String password = req.getParameter("newPassword"); // 确保获取到正确的注册密码

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        if (registerService.register(user)) {
            // 注册成功，重定向到登录页面，并携带提示信息
            req.setAttribute("successMessage", "注册成功，请登录");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } else {
            // 注册失败，提示错误信息并返回登录页面
            req.setAttribute("errorMessage", "注册失败，请重试");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
