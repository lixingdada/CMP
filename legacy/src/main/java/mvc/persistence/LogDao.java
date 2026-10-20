package mvc.persistence;

public interface LogDao {
    int loginLog(String username,String details);   //登录记录日志

    int logoutLog(String username,String details);  //退出登录记录日志

    int browseLog(String username,String itemId,String details);   //浏览商品记录日志

    int addCart(String username,String itemId,String details);    //加入购物车记录日志

    int deleteCart(String username,String itemId,int cartId);    //删除购物车记录日志

    int generateOrders(String username,String itemId,String details);    //生成订单

    int payOrder(String username,String itemId ,String details);     //支付了订单

    int cancelOrders(String username,String itemId,int orderId);    //取消订单

}
