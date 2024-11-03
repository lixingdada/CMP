package mvc.web.servlet;


import mvc.service.LoginService;
import mvc.domain.User;
import org.w3c.dom.ls.LSOutput;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/loginForm"})
public class LoginServlet extends HttpServlet {
    private LoginService loginService;

    @Override
    public void init() throws ServletException {
        loginService = new LoginService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 检查用户是否存在
        if (!loginService.userExists(username)) {
            // 用户不存在，返回登录页面并提示错误信息
            req.setAttribute("errorMessage", "该用户不存在，请先完成注册");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } else if (loginService.login(username, password)) {
            // 登录成功
            HttpSession session = req.getSession();
            User user = loginService.getUser(username, password);
            session.setAttribute("user", user);
            resp.sendRedirect("main"); // 登录后重定向到主页面
        } else {
            // 用户存在，但密码不正确，返回登录页面并提示错误信息
            req.setAttribute("errorMessage", "用户名或密码错误");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
