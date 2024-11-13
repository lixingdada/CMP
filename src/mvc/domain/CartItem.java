package mvc.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class CartItem implements Serializable {

    public Item item;   //商品类型

    private int quantity;   //商品数量
    public BigDecimal total;    //总价

    public BigDecimal getTotal() { // 添加 getter 方法
        return total;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        calculateTotal();
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

//计算总价
    public void incrementQuantity() {
        this.quantity++;
        calculateTotal();
    }

    private void calculateTotal() {
        if (this.item != null && this.item.getListPrice() != null) {
            this.total = this.item.getListPrice().multiply(new BigDecimal(this.quantity));
        } else {
            this.total = null;
        }
    }
}
