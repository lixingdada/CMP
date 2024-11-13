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
import java.io.PrintWriter;

@WebServlet(name = "EditAddressServlet",urlPatterns = {"/editAddress"})
public class EditAddressServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String receiverName = req.getParameter("receiverName");
        String receiverPhone = req.getParameter("receiverPhone");
        String receiverAddress = req.getParameter("receiverAddress");
        System.out.println(receiverName);


        String oldReceiverName = req.getParameter("oldReceiverName");
        String oldReceiverPhone = req.getParameter("oldReceiverPhone");
        String oldReceiverAddress = req.getParameter("oldReceiverAddress");
        System.out.println(oldReceiverName);

        UserInfoService userInfoService = new UserInfoService();
        String message = userInfoService.editAddress(user,receiverName,receiverPhone,receiverAddress,user.getUsername(),oldReceiverName,oldReceiverPhone,oldReceiverAddress);
        req.setAttribute("message",message);

        req.getRequestDispatcher("/WEB-INF/jsp/user/userInfo.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String receiverName = req.getParameter("receiverName");
        String receiverPhone = req.getParameter("receiverPhone");
        String receiverAddress = req.getParameter("receiverAddress");

        UserInfoService userInfoService = new UserInfoService();
      String flag = req.getParameter("flag");
        if(flag!=null&&flag.equals("delete")){
            userInfoService.deleteAddress(user,user.getUsername(),receiverName,receiverPhone,receiverAddress);
            req.getRequestDispatcher("/WEB-INF/jsp/user/userInfo.jsp").forward(req,resp);
            return;
        }

        String message =  userInfoService.insertAddress(user,user.getUsername(),receiverName,receiverPhone,receiverAddress);
        System.out.println("message"+message);
        req.setAttribute("message",message);
        /*
        resp.setContentType("application/xml");
        PrintWriter out = resp.getWriter();

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<response>");
        if (message!=null||!message.equals("")) {
            out.println("<success>false</success>");
            out.println("<message>该地址信息已存在，请使用其他地址！</message>");
        } else {
            // 处理添加地址的逻辑
            //req.getRequestDispatcher("/WEB-INF/jsp/user/userInfo.jsp").forward(req,resp);
            out.println("<success>true</success>");
        }
        out.println("</response>");

        out.flush();
        */
        req.getRequestDispatcher("/WEB-INF/jsp/user/userInfo.jsp").forward(req,resp);
    }
}
