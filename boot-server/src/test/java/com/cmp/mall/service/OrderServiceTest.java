package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.CreateOrderRequest;
import com.cmp.mall.dto.OrderView;
import com.cmp.mall.entity.Cart;
import com.cmp.mall.entity.CartItem;
import com.cmp.mall.entity.Item;
import com.cmp.mall.entity.Order;
import com.cmp.mall.entity.OrderItem;
import com.cmp.mall.mapper.CartMapper;
import com.cmp.mall.mapper.ItemMapper;
import com.cmp.mall.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 下单相关的单测。只测 Service 自己的判断逻辑，数据库全部 mock 掉。
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CartMapper cartMapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private OrderService orderService;

    private static final CreateOrderRequest REQUEST =
            new CreateOrderRequest("张三", "长沙市岳麓区", "13800138000");

    private Cart cart() {
        Cart c = new Cart();
        c.setCartId(7);
        c.setUsername("test");
        return c;
    }

    private CartItem line(String itemId, String name, String price, int qty) {
        CartItem it = new CartItem();
        it.setCartId(7);
        it.setItemId(itemId);
        it.setItemName(name);
        it.setItemPrice(new BigDecimal(price));
        it.setQuantity(qty);
        return it;
    }

    private Item item(String itemId, String price, int stock) {
        Item i = new Item();
        i.setItemId(itemId);
        i.setListPrice(new BigDecimal(price));
        i.setQty(stock);
        i.setAttr3("天使鱼 大型");
        return i;
    }

    @Test
    @DisplayName("没有购物车记录时报购物车为空")
    void createWithoutCart() {
        when(cartMapper.findByUsername("test")).thenReturn(null);

        assertThatThrownBy(() -> orderService.create("test", REQUEST))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.CART_EMPTY);
    }

    @Test
    @DisplayName("购物车是空的时候不下单")
    void createWithEmptyCart() {
        when(cartMapper.findByUsername("test")).thenReturn(cart());
        when(cartMapper.findByCartId(7)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.create("test", REQUEST))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.CART_EMPTY);

        verify(orderMapper, never()).insertOrder(any());
    }

    @Test
    @DisplayName("库存不足时抛异常，而且订单主表根本不会写")
    void createWhenStockNotEnough() {
        when(cartMapper.findByUsername("test")).thenReturn(cart());
        when(cartMapper.findByCartId(7)).thenReturn(List.of(line("EST-1", "天使鱼", "10.00", 5)));
        when(itemMapper.findById("EST-1")).thenReturn(item("EST-1", "10.00", 2));
        // 条件更新影响 0 行，说明库存不够
        when(itemMapper.deductStock("EST-1", 5)).thenReturn(0);

        assertThatThrownBy(() -> orderService.create("test", REQUEST))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.OUT_OF_STOCK);

        verify(orderMapper, never()).insertOrder(any());
        verify(cartMapper, never()).deleteByCartId(anyInt());
    }

    @Test
    @DisplayName("明细价格取下单时的购物车价格，不是商品当前价")
    void createUsesPriceSnapshot() {
        when(cartMapper.findByUsername("test")).thenReturn(cart());
        when(cartMapper.findByCartId(7)).thenReturn(List.of(line("EST-1", "天使鱼", "9.90", 2)));
        when(itemMapper.findById("EST-1")).thenReturn(item("EST-1", "12.00", 100));
        when(itemMapper.deductStock("EST-1", 2)).thenReturn(1);
        doAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setOrderId(88);
            return 1;
        }).when(orderMapper).insertOrder(any());

        orderService.create("test", REQUEST);

        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderMapper).insertOrderItems(captor.capture());

        List<OrderItem> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getPrice()).isEqualByComparingTo("9.90");
        assertThat(saved.get(0).getOrderId()).isEqualTo(88);
        verify(cartMapper).deleteByCartId(7);
    }

    @Test
    @DisplayName("数量要按购物车里的条数原样写进明细")
    void createWritesAllLines() {
        when(cartMapper.findByUsername("test")).thenReturn(cart());
        when(cartMapper.findByCartId(7)).thenReturn(List.of(
                line("EST-1", "天使鱼", "9.90", 2),
                line("EST-2", "暹罗猫", "199.00", 1)));
        when(itemMapper.findById(anyString())).thenAnswer(inv ->
                item(inv.getArgument(0), "0", 50));
        when(itemMapper.deductStock(anyString(), anyInt())).thenReturn(1);
        doAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setOrderId(99);
            return 1;
        }).when(orderMapper).insertOrder(any());

        OrderView view = orderService.create("test", REQUEST);

        assertThat(view).isNotNull();
        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderMapper).insertOrderItems(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).getItemId()).isEqualTo("EST-1");
        assertThat(captor.getValue().get(1).getItemId()).isEqualTo("EST-2");
    }

    @Test
    @DisplayName("查别人的订单会拦下来")
    void getOrderOfAnotherUser() {
        Order o = new Order();
        o.setOrderId(1);
        o.setUserId("someone-else");
        when(orderMapper.findById(1)).thenReturn(o);

        assertThatThrownBy(() -> orderService.getOrder("test", 1))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("订单不存在")
    void getOrderNotFound() {
        when(orderMapper.findById(eq(404))).thenReturn(null);

        assertThatThrownBy(() -> orderService.getOrder("test", 404))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }
}
