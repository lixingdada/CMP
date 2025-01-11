package mvc.service;

import mvc.persistence.Impl.LogDaoImpl;
import mvc.persistence.LogDao;

public class LogService {
    private LogDao logDao;

    public LogService(){
        this.logDao = new LogDaoImpl();
    }

    public int loginLog(String username,String details){
        return logDao.loginLog(username,details);
    }

    public int logoutLog(String username,String details){
        return logDao.logoutLog(username,details);
    }

    public int browseLog(String username,String itemId,String details ){
        return logDao.browseLog(username, itemId, details);
    }

    public int addCart(String username,String itemId,String details ){
        return logDao.addCart(username, itemId, details);
    }

    public int deleteCart(String username,String itemId,int cartId){
        return logDao.deleteCart(username, itemId, cartId);
    }

    public int generateOrders(String username,String itemId,String details ){
        return logDao.generateOrders(username, itemId, details);
    }

    public int cancelOrders(String username,String itemId,int orderId){
        return logDao.cancelOrders(username, itemId, orderId);
    }

    public int payOrder(String username,String itemId,String details){
        return logDao.payOrder(username,itemId,details);
    }

}
