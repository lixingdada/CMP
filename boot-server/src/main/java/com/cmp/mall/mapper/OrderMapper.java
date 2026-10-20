package com.cmp.mall.mapper;

import com.cmp.mall.entity.Order;
import com.cmp.mall.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 订单聚合：orders（主）+ order_items（明细） */
@Mapper
public interface OrderMapper {

    /** 插入订单主表并回填自增主键，供明细表引用 */
    int insertOrder(Order order);

    int insertOrderItems(@Param("items") List<OrderItem> items);

    Order findById(@Param("orderId") Integer orderId);

    List<Order> findByUserId(@Param("userId") String userId);

    List<OrderItem> findItemsByOrderId(@Param("orderId") Integer orderId);

    /** 明细行 + 商品名（item → product 两跳关联），用于前端展示 */
    List<com.cmp.mall.dto.OrderItemView> findItemViewsByOrderId(@Param("orderId") Integer orderId);

    /**
     * 一次取回多个订单的明细。列表页先用 PageHelper 查出当页订单，
     * 再用这条 IN 查询把明细一次性捞回来，避免按订单逐条查造成 N+1。
     */
    List<OrderItem> findItemsByOrderIds(@Param("orderIds") List<Integer> orderIds);

    /** 统计某用户某商品的历史购买总数，供报表接口使用 */
    int sumQuantityByUserAndItem(@Param("userId") String userId, @Param("itemId") String itemId);
}
