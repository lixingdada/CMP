package mvc.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyOrder implements Serializable {

    public List<Order> orderList = new ArrayList<Order>();

    public List<Order> getOrderList() {
        return orderList;
    }

    public void addOrder(Order order) {
        orderList.add(order);
    }
}
