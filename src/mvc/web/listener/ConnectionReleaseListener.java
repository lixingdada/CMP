package mvc.web.listener;

import mvc.persistence.DBUtil;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * 请求结束时归还数据库连接。
 *
 * <p>连接改成请求级复用之后，每条连接的所有权归属于处理该请求的线程（见
 * {@code ConnectionContext}）。Servlet 容器会复用线程，所以必须在请求结束时手动解绑，
 * 否则连接会被一直占着：池子很快耗尽，下一个请求再来就只能等超时。
 *
 * <p>这里用 {@link ServletRequestListener} 而不是 Filter，是因为它不依赖 URL 映射，
 * 包括静态资源请求在内的所有请求都会被通知，不会再出现"某个路径漏了归还"的情况。
 */
@WebListener
public class ConnectionReleaseListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // 上一个请求若因异常没有清理干净，这里先兜底清一次，避免脏连接被当前请求继承
        DBUtil.releaseThreadConnection();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        DBUtil.releaseThreadConnection();
    }
}
