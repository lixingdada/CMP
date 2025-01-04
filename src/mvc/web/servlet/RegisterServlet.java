package mvc.web.servlet;

import mvc.domain.User;
import mvc.persistence.Impl.UserDaoImpl;
import mvc.persistence.UserDao;
import mvc.service.LogService;
import mvc.service.LoginService;
import mvc.service.RegisterService;
import mvc.service.UserInfoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/registerForm"})
public class RegisterServlet extends HttpServlet {
    private RegisterService registerService;
    private LogService logService;
    private UserInfoService userInfoService;

    @Override
    public void init() throws ServletException {
        registerService = new RegisterService();
        logService = new LogService();
        userInfoService = new UserInfoService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置响应类型为 JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        PrintWriter out = resp.getWriter();

        // 检查用户名是否为空
        if (username == null || username.trim().isEmpty()) {
            out.print("{\"exists\": false, \"message\": \"用户名不能为空\"}");
            out.flush();
            return;
        }

        // 检查用户名是否存在
        UserDaoImpl userDAO = new UserDaoImpl();
        boolean exists = userDAO.userExists(username.trim());

        // 构建 JSON 响应
        StringBuilder jsonResponse = new StringBuilder();
        jsonResponse.append("{");
        jsonResponse.append("\"exists\":").append(exists).append(",");
        if (exists) {
            jsonResponse.append("\"message\":\"⚠ 用户名已存在，请选择其他用户名\"");
        } else {
            jsonResponse.append("\"message\":\"😊用户名可用\"");
        }
        jsonResponse.append("}");

        out.print(jsonResponse.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("newUsername"); // 确保获取到正确的注册用户名
        String password = req.getParameter("newPassword"); // 确保获取到正确的注册密码

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password.trim());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        StringBuilder jsonResponse = new StringBuilder();
        jsonResponse.append("{");

        // 后端再次验证用户名是否存在

        UserDaoImpl userDAO = new UserDaoImpl();
        if (userDAO.userExists(user.getUsername())) {
            jsonResponse.append("\"success\":false,");
            jsonResponse.append("\"message\":\"⚠ 注册失败，用户名已存在\"");
            jsonResponse.append("}");
            out.print(jsonResponse.toString());
            out.flush();
            return;
        }

        // 尝试插入新用户
        boolean registrationSuccess = registerService.register(user);

        if (registrationSuccess) {
            // 注册成功，执行后续操作
            registerService.createAccount(user.getUsername());

            jsonResponse.append("\"success\":true,");
            jsonResponse.append("\"message\":\"😊 注册成功，请登录。\"");
            // 如果有重定向地址，可在前端处理或通过 AJAX 完成
        } else {
            // 注册失败，返回错误信息
            jsonResponse.append("\"success\":false,");
            jsonResponse.append("\"message\":\"⚠ 注册失败，请重试。\"");
        }

        jsonResponse.append("}");

        out.print(jsonResponse.toString());
        out.flush();
    }
}
