package mvc.web.servlet;


import mvc.service.LogService;
import mvc.service.LoginService;
import mvc.domain.User;
import mvc.service.UserInfoService;
import org.w3c.dom.ls.LSOutput;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
@WebServlet(name = "LoginServlet", urlPatterns = {"/loginForm"})
public class LoginServlet extends HttpServlet {
    private LoginService loginService;
    private LogService logService;
    private UserInfoService userInfoService;

    @Override
    public void init() throws ServletException {
        loginService = new LoginService();
        logService = new LogService();
        userInfoService = new UserInfoService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        session.setAttribute("username", username);

        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String inputCaptcha = req.getParameter("captcha");
        String sessionCaptcha = (String) req.getSession().getAttribute("captcha");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // 检查验证码是否正确
        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(inputCaptcha)) {
            String jsonResponse = "{\"success\":false,\"message\":\"⚠ 验证码错误\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        }

        // 检查用户是否存在
        if (!loginService.userExists(username)) {
            String jsonResponse = "{\"success\":false,\"message\":\"⚠ 该用户不存在，请先完成注册\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        } else if (loginService.login(username, password)) {
            HttpSession session = req.getSession();
            User user = loginService.getUser(username, password);

            session.setAttribute("username", username);

            userInfoService.loadUser(user, username);
            userInfoService.findAddress(user, username);

            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isLogIn", true); // 标记用户登录状态

            // 手动构建 JSON 响应
            String jsonResponse = "{\"success\":true,\"message\":\"登录成功！\",\"redirect\":\"main\"}";
            out.print(jsonResponse);
            ((PrintWriter) out).flush();
        } else {
            String jsonResponse = "{\"success\":false,\"message\":\"⚠ 用户名或密码错误\"}";
            out.print(jsonResponse);
            out.flush();
        }
    }
}
