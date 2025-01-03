package mvc.web.servlet;

import com.mysql.cj.Session;
import mvc.domain.User;
import mvc.service.CatalogService;
import mvc.service.UserInfoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@MultipartConfig // 用于支持文件上传
@WebServlet(name = "uploadAvatarServlet" , urlPatterns = {"/uploadAvatar"})
public class uploadAvatarServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //其实就是存在编译后的文件夹里面
        String uploadPath = getServletContext().getRealPath("/images");  // 获取应用根目录下的 /images 目录路径
        File uploadDir = new File(uploadPath);
        req.setCharacterEncoding("UTF-8");  // 设置请求编码为 UTF-8
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        UserInfoService userInfoService = new UserInfoService();


// 如果上传目录不存在，创建它
        if (!uploadDir.exists()) {
            uploadDir.mkdir();  // 创建 /images 目录
        }

        try {
            for (Part part : req.getParts()) {
                String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                    // 写入文件到指定目录
                    part.write(uploadPath + File.separator + fileName);
                    userInfoService.updateAvatar(fileName,user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("文件上传失败：" + e.getMessage());
        }
    }
    }