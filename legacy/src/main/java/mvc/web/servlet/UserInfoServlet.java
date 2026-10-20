package mvc.web.servlet;

import mvc.domain.User;
import mvc.service.UserInfoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
//import java.sql.Date;
import java.time.LocalDate;

@WebServlet(name = "UserInfoServlet",urlPatterns = {"/userInfo"})
public class UserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if(user==null){
            req.getRequestDispatcher("loginForm").forward(req,resp);
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/user/userInfo.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");

        if(user==null){
            req.getRequestDispatcher("loginForm").forward(req,resp);
            return;
        }
        String virtualName = req.getParameter("virtualName");
        LocalDate birthday = LocalDate.parse(req.getParameter("birthday"));
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");


        String username = user.getUsername();

        UserInfoService userInfoService = new UserInfoService();
        userInfoService.updateUserInfo(virtualName,birthday,email,phone,username);
        userInfoService.loadUser(user,username);
        
//        req.getRequestDispatcher("/WEB-INF/jsp/user/userInfo.jsp").forward(req,resp);
    }
}
