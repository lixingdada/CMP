package mvc.web.servlet;

import mvc.domain.Item;
import mvc.domain.Product;
import mvc.domain.User;
import mvc.service.CatalogService;
import mvc.service.LogService;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class UserSessionLogger implements HttpSessionListener, HttpSessionAttributeListener {
    private CatalogService catalogService = new CatalogService();
    private LogService logService = new LogService();
    /*@Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("监听器：session清除");
        String userName = (String) se.getSession().getAttribute("username");
                if (userName != null) {
                    System.out.println("监听器：有用户退出登录！");
                    LogService logService = new LogService();
                    logService.logoutLog(userName,userName+"退出了登录！");
                }
            }
*/
    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        /*if ("isLogIn".equals(event.getName())){
            Boolean isLogIn = (Boolean) event.getValue();
            if(isLogIn!=null&&!isLogIn){
                System.out.println("监听器：用户退出");
                String userName = (String) event.getSession().getAttribute("username");
                System.out.println("username: "+userName);
                LogService logService = new LogService();
                logService.logoutLog(userName, userName + "退出了登录！");
            }
        }*/

        if ("user".equals(event.getName())) {
            User user = (User) event.getValue();
            String userName = user.getUsername();
            if (userName != null) {
                System.out.println("监听器：用户退出");
                logService.logoutLog(userName, userName + "退出了登录！");
            }
        }

    }


    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {

        System.out.println("Attribute added: " + event.getName() + " = " + event.getValue());

        if ("isLogIn".equals(event.getName())){
            Boolean isLogIn = (Boolean) event.getValue();
            if(isLogIn!=null&&isLogIn){
                System.out.println("监听器：用户登录");
                String userName = (String) event.getSession().getAttribute("username");
                if (userName != null) {
                    LogService logService = new LogService();
                    logService.loginLog(userName,userName+"刚刚登陆了！");
                }
            }
        }

       /* if ("item".equals(event.getName())){
                System.out.println("监听器：浏览商品");
                String username = (String) event.getSession().getAttribute("username");
                Item item =(Item) event.getSession().getAttribute("item");
            if (item!=null&&username!=null && !username.equals("")) {
                int supplier = item.getSupplierId();
                Product product = catalogService.getProduct(item.getProductId());
                String productName = product.getName();

                logService.browseLog(username, item.getItemId(), username + "浏览了由" + supplier + "提供的" + productName);
            }
        }*/
    }
}
