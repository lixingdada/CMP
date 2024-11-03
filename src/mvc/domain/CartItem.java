package mvc.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class CartItem implements Serializable {

    private Item item;   //商品类型

    private int quantity;   //商品数量
    private BigDecimal total;    //总价

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        calculateTotal();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

//计算总价
    public void incrementQuantity() {
        quantity++;
        calculateTotal();
    }

    private void calculateTotal() {
        if (item != null && item.getListPrice() != null) {
            total = item.getListPrice().multiply(new BigDecimal(quantity));
        } else {
            total = null;
        }
    }
}
