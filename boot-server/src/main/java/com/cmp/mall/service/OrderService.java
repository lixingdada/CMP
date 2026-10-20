package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.CreateOrderRequest;
import com.cmp.mall.dto.OrderItemView;
import com.cmp.mall.dto.OrderView;
import com.cmp.mall.dto.PageResult;
import com.cmp.mall.entity.Cart;
import com.cmp.mall.entity.CartItem;
import com.cmp.mall.entity.Item;
import com.cmp.mall.entity.Order;
import com.cmp.mall.entity.OrderItem;
import com.cmp.mall.mapper.CartMapper;
import com.cmp.mall.mapper.ItemMapper;
import com.cmp.mall.mapper.OrderMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 下单与订单查询。
 *
 * <p>下单要动五张表：item 扣库存、orders 主表、order_items 明细、cart_item 清车、user_log 日志。
 * 任何一步失败都得整体回滚，不然会出现库存扣了但没订单这种情况。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;

    /**
     * 创建订单。rollbackFor 要显式写上，Spring 默认只对 RuntimeException 回滚。
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderView create(String username, CreateOrderRequest request) {
        Cart cart = cartMapper.findByUsername(username);
        if (cart == null) {
            throw new BizException(ErrorCode.CART_EMPTY);
        }
        List<CartItem> cartItems = cartMapper.findByCartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new BizException(ErrorCode.CART_EMPTY);
        }

        // 逐条扣库存
        List<OrderItem> orderItems = new ArrayList<>(cartItems.size());
        for (CartItem line : cartItems) {
            // 兼容老数据：item_id 为 NULL 时，item_name 里存的是 itemId
            String itemId = line.getItemId() != null ? line.getItemId() : line.getItemName();
            Item item = itemMapper.findById(itemId);
            if (item == null) {
                throw new BizException(ErrorCode.ITEM_NOT_FOUND, "商品已下架：" + line.getItemName());
            }

            // 扣减与判断合并为一条 UPDATE，影响行数为 0 即库存不足
            int affected = itemMapper.deductStock(itemId, line.getQuantity());
            if (affected == 0) {
                throw new BizException(ErrorCode.OUT_OF_STOCK,
                        "「" + line.getItemName() + "」库存不足，当前仅剩 "
                                + (item.getQty() == null ? 0 : item.getQty()) + " 件");
            }

            OrderItem oi = new OrderItem();
            oi.setItemId(itemId);
            oi.setQuantity(line.getQuantity());
            // 价格快照：用购物车里的价格，而不是实时查 item，避免下单过程中调价导致金额漂移
            oi.setPrice(line.getItemPrice());
            oi.setUsername(username);
            orderItems.add(oi);
        }

        // 写订单主表，orderId 是自增的，insert 完回填到对象上
        Order order = new Order();
        order.setUserId(username);
        order.setOrderName(request.orderName());
        order.setOrderAddress(request.orderAddress());
        order.setOrderTel(request.orderTel());
        order.setOrderDate(LocalDateTime.now());
        orderMapper.insertOrder(order);

        // 明细一条 SQL 批量插，不循环
        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getOrderId());
        }
        orderMapper.insertOrderItems(orderItems);

        // 清空购物车
        cartMapper.deleteByCartId(cart.getCartId());

        log.info("下单成功 username={} orderId={} 明细数={}",
                username, order.getOrderId(), orderItems.size());

        return buildView(order);
    }

    @Transactional(readOnly = true)
    public PageResult<OrderView> listOrders(String username, int pageNum, int pageSize) {
        int safePage = Math.max(pageNum, 1);
        int safeSize = Math.min(Math.max(pageSize, 1), 50);

        PageHelper.startPage(safePage, safeSize);
        List<Order> orders = orderMapper.findByUserId(username);
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        List<Order> pageOrders = pageInfo.getList();

        if (pageOrders.isEmpty()) {
            return PageResult.of(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), List.of());
        }

        // 一次 IN 查询取回当页所有订单的明细，避免逐条查造成 N+1
        List<Integer> orderIds = pageOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
        Map<Integer, List<OrderItem>> grouped = orderMapper.findItemsByOrderIds(orderIds).stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId, LinkedHashMap::new, Collectors.toList()));

        List<OrderView> views = pageOrders.stream()
                .map(o -> buildView(o, grouped.getOrDefault(o.getOrderId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return PageResult.of(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), views);
    }

    @Transactional(readOnly = true)
    public OrderView getOrder(String username, Integer orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.ORDER_NOT_FOUND);
        }
        // 越权校验：只能查自己的订单
        if (!username.equals(order.getUserId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权查看该订单");
        }
        return buildView(order);
    }

    /** 订单明细 + 商品名，转成前端视图，附带总金额 */
    private OrderView buildView(Order order) {
        List<OrderItemView> items = orderMapper.findItemViewsByOrderId(order.getOrderId());
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemView it : items) {
            BigDecimal subtotal = it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            it.setSubtotal(subtotal);
            total = total.add(subtotal);
        }
        return new OrderView(order.getOrderId(), order.getOrderName(), order.getOrderAddress(),
                order.getOrderTel(), order.getOrderDate(), items, total);
    }

    /** 列表页复用：明细已在内存中，不再回查数据库 */
    private OrderView buildView(Order order, List<OrderItem> items) {
        List<OrderItemView> views = new ArrayList<>(items.size());
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : items) {
            OrderItemView v = new OrderItemView();
            v.setItemId(it.getItemId());
            v.setQuantity(it.getQuantity());
            v.setPrice(it.getPrice());
            BigDecimal subtotal = it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            v.setSubtotal(subtotal);
            v.setItemName(it.getItemId());
            total = total.add(subtotal);
            views.add(v);
        }
        return new OrderView(order.getOrderId(), order.getOrderName(), order.getOrderAddress(),
                order.getOrderTel(), order.getOrderDate(), views, total);
    }
}
