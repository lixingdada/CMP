package mvc.persistence.Impl;

import mvc.persistence.DBUtil;
import mvc.persistence.LogDao;

import java.sql.ResultSet;
import java.util.ArrayList;

public class LogDaoImpl implements LogDao {
    String LOGIN_LOG = "INSERT INTO user_log (user_id,action,details) VALUES (?,?,?)";
    String BROWSE_LOG = "INSERT INTO user_log (user_id,action,item_id,details) VALUES (?,?,?,?)";
    String  DELETE_CART= "DELETE from user_log WHERE  user_id = ? AND item_id = ?  AND cart_id = ?";

    String  PAY_ORDER= "INSERT INTO user_log (user_id,action,item_id,details) VALUES (?,?,?,?)";
    String  CANCEL_ORDER= "DELETE from user_log WHERE  user_id = ? AND item_id = ?  AND order_id = ?";
    @Override
    public int loginLog(String username,  String details) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add("登录");
        arrayList.add(details);
        return DBUtil.executeUpdate(LOGIN_LOG,arrayList);
    }

    @Override
    public int logoutLog(String username, String details) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add("退出登录");
        arrayList.add(details);
        return DBUtil.executeUpdate(LOGIN_LOG,arrayList);
    }

    @Override
    public int browseLog(String username,  String itemId, String details) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add("浏览商品");
        arrayList.add(itemId);
        arrayList.add(details);
        return DBUtil.executeUpdate(BROWSE_LOG,arrayList);
    }

    @Override
    public int addCart(String username, String itemId, String details) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add("加入购物车");
        arrayList.add(itemId);
        arrayList.add(details);
        return DBUtil.executeUpdate(BROWSE_LOG,arrayList);
    }

    @Override
    public int deleteCart(String username, String itemId, int caryId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add(itemId);
        arrayList.add(caryId);
        return DBUtil.executeUpdate(DELETE_CART,arrayList);
    }

    @Override
    public int generateOrders(String username,String itemId, String details) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add("提交订单");
        arrayList.add(itemId);
        arrayList.add(details);
        return DBUtil.executeUpdate(BROWSE_LOG,arrayList);
    }

    @Override
    public int payOrder(String username, String itemId,String details) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add("支付订单");
        arrayList.add(itemId);
        arrayList.add(details);
        return DBUtil.executeUpdate(PAY_ORDER,arrayList);
    }

    @Override
    public int cancelOrders(String username, String itemId,int orderId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add(itemId);
        arrayList.add(orderId);
        return DBUtil.executeUpdate(CANCEL_ORDER,arrayList);
    }
}
