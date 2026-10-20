package mvc.persistence;

import mvc.domain.MyOrder;
import mvc.domain.Order;
import mvc.domain.OrderItem;

import java.util.List;

public interface OrderDao {

        void saveOrder(Order order,String username);

        List<Order> getAllOrders(String username);

        public List<OrderItem> findOrderItemsByOrderId(int orderId,String username);
}
