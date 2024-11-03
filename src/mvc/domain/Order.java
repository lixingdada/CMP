//package mvc.domain;
//
//import mvc.domain.Cart;
//import mvc.domain.CartItem;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
//public class Order implements Serializable {
//
//    //private static final long serialVersionUID = 6321792448424424931L;
//
//    private int orderId;
//    private int userId;
//    private String orderName;
//    private Date orderDate;
//    private String orderAddress;
//    private String OrderTel;
//    private Cart cart;
//
//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    public int getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(int orderId) {
//        this.orderId = orderId;
//    }
//
//    public Date getOrderDate() {
//        return orderDate;
//    }
//
//    public void setOrderDate(Date orderDate) {
//        this.orderDate = orderDate;
//    }
//
//    public String getOrderAddress() {
//        return orderAddress;
//    }
//
//    public void setOrderAddress(String orderAddress) {
//        this.orderAddress = orderAddress;
//    }
//
//    public String getOrderName() {
//        return orderName;
//    }
//
//    public void setOrderName(String orderName) {
//        this.orderName = orderName;
//    }
//
//    public String getOrderTel() {
//        return OrderTel;
//    }
//
//    public void setOrderTel(String orderTel) {
//        OrderTel = orderTel;
//    }
//
//    public Cart getCart() {
//        return cart;
//    }
//
//    public void setCart(Cart cart) {
//        this.cart = cart;
//    }
//
//    public Order(){}
//
//    public Order(User user, Cart cart) {
//        orderName = user.getUsername();
//        orderDate = new Date();
//
//    }
//
//}
//
