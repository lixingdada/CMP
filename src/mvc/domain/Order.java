package mvc.domain;

import mvc.domain.Cart;
import mvc.domain.CartItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {

    private int orderId;
    private String userId;
    private String orderName;
    private Date orderDate;
    private String orderAddress;
    private String orderTel;
    private Cart cart;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderTel() {
        return orderTel;
    }

    public void setOrderTel(String orderTel) {
        this.orderTel = orderTel;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Order() {}

    public Order(User user, Cart cart) {
        orderName = user.getUsername();
        orderDate = new Date();
        this.cart = cart;
    }

    // 新增方法：将购物车中的商品添加到订单中
    public void populateFromCart() {
        if (this.cart != null && this.cart.getCartItemList() != null) {
            for (CartItem item : this.cart.getCartItemList()) {
                // 这里可以进行进一步处理，例如将商品信息存储到订单中
            }
        }
    }
}
