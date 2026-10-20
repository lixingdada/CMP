package mvc.web.listener;

import mvc.persistence.DBUtil;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * 请求结束时归还数据库连接。
 *
 * <p>连接现在是请求级的，所有权在处理该请求的线程上（见 ConnectionContext）。
 * 容器会复用线程，所以请求一结束就得解绑，否则连接一直被占着，池子很快见底。
 *
 * <p>用 ServletRequestListener 而没用 Filter，是因为它不依赖 URL 映射，
 * 静态资源请求也会被通知到，不会出现某个路径漏还的情况。
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
