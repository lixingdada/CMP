package mvc.web.servlet;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
@WebServlet(name = "CaptchaServlet", urlPatterns = {"/captchaServlet"})
public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 禁用缓存
        resp.setHeader("pragma", "No-cache");
        resp.setHeader("Cache-Control", "No-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/gif");

        int width = 100;
        int height = 40;

        // 创建画布和画笔
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) image.getGraphics();

        // 设置背景色为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height); // 填充背景为白色

        // 设置字体
        g.setFont(new Font("仿宋", Font.BOLD, 25));

//        // 绘制带圆角的边框
//        g.setColor(Color.BLACK); // 设置边框颜色
//        g.setStroke(new BasicStroke(2)); // 设置边框宽度
//        g.drawRoundRect(0, 0, width - 1, height - 1, 15, 15); // 绘制圆角矩形，最后两个参数为圆角的弯曲度

        // 生成验证码字符
        String source = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder captchaBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            char ch = source.charAt(random.nextInt(source.length()));
            captchaBuilder.append(ch);

            // 设置字符颜色并绘制
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(String.valueOf(ch), 15 + i * 20, 30);
        }

        // 添加干扰线
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 保存验证码到 session
        String captchaCode = captchaBuilder.toString();
        req.getSession().setAttribute("captcha", captchaCode);

        // 输出图像
        g.dispose();
        try (OutputStream out = resp.getOutputStream()) {
            ImageIO.write(image, "gif", out);
            out.flush();
        }
    }
}