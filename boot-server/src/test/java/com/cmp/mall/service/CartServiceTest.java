package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.CartLineView;
import com.cmp.mall.dto.CartView;
import com.cmp.mall.entity.Cart;
import com.cmp.mall.entity.CartItem;
import com.cmp.mall.entity.Item;
import com.cmp.mall.mapper.CartMapper;
import com.cmp.mall.mapper.ItemMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartMapper cartMapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart() {
        Cart c = new Cart();
        c.setCartId(7);
        c.setUsername("test");
        return c;
    }

    private Item item(String id, String price, int stock) {
        Item i = new Item();
        i.setItemId(id);
        i.setListPrice(new BigDecimal(price));
        i.setQty(stock);
        i.setAttr3("天使鱼 大型");
        return i;
    }

    @Test
    @DisplayName("同一商品重复加购是累加，不新增一行")
    void addItemAccumulates() {
        when(itemMapper.findById("EST-1")).thenReturn(item("EST-1", "9.90", 100));
        when(cartMapper.findByUsername("test")).thenReturn(cart());

        CartItem exist = new CartItem();
        exist.setCartId(7);
        exist.setItemId("EST-1");
        exist.setItemName("天使鱼 大型");
        exist.setItemPrice(new BigDecimal("9.90"));
        exist.setQuantity(2);
        when(cartMapper.findItemByCartAndItemId(7, "EST-1")).thenReturn(exist);

        cartService.addItem("test", "EST-1", 3);

        verify(cartMapper).updateItemQuantity(7, "天使鱼 大型", 5);
        verify(cartMapper, never()).insertItem(any());
    }

    @Test
    @DisplayName("老数据 item_id 为空时按名称也能找到那一行")
    void addItemFallsBackToName() {
        when(itemMapper.findById("EST-1")).thenReturn(item("EST-1", "9.90", 100));
        when(cartMapper.findByUsername("test")).thenReturn(cart());
        when(cartMapper.findItemByCartAndItemId(7, "EST-1")).thenReturn(null);

        CartItem legacy = new CartItem();
        legacy.setCartId(7);
        legacy.setItemId(null);
        legacy.setItemName("EST-1");
        legacy.setItemPrice(new BigDecimal("9.90"));
        legacy.setQuantity(4);

        when(cartMapper.findItemByCartAndName(eq(7), anyString()))
                .thenAnswer(inv -> "EST-1".equals(inv.getArgument(1)) ? legacy : null);

        cartService.addItem("test", "EST-1", 1);

        verify(cartMapper).updateItemQuantity(7, "EST-1", 5);
        verify(cartMapper, never()).insertItem(any());
    }

    @Test
    @DisplayName("加上去以后超过库存就不让加")
    void addItemOverStock() {
        when(itemMapper.findById("EST-1")).thenReturn(item("EST-1", "9.90", 3));
        when(cartMapper.findByUsername("test")).thenReturn(cart());

        CartItem exist = new CartItem();
        exist.setCartId(7);
        exist.setItemId("EST-1");
        exist.setItemName("天使鱼 大型");
        exist.setQuantity(2);
        when(cartMapper.findItemByCartAndItemId(7, "EST-1")).thenReturn(exist);

        assertThatThrownBy(() -> cartService.addItem("test", "EST-1", 2))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.OUT_OF_STOCK);

        verify(cartMapper, never()).updateItemQuantity(anyInt(), anyString(), anyInt());
    }

    @Test
    @DisplayName("数量小于等于 0 直接拒掉")
    void addItemWithInvalidQuantity() {
        assertThatThrownBy(() -> cartService.addItem("test", "EST-1", 0))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.PARAM_INVALID);

        verify(itemMapper, never()).findById(anyString());
    }

    @Test
    @DisplayName("商品不存在时不建车")
    void addItemWithUnknownItem() {
        when(itemMapper.findById("NOPE")).thenReturn(null);

        assertThatThrownBy(() -> cartService.addItem("test", "NOPE", 1))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.ITEM_NOT_FOUND);

        verify(cartMapper, never()).insertCart(anyString());
    }

    @Test
    @DisplayName("没有购物车记录时返回空车，而不是报错")
    void getCartWithoutRecord() {
        when(cartMapper.findByUsername("test")).thenReturn(null);

        CartView view = cartService.getCart("test");

        assertThat(view.isEmpty()).isTrue();
        assertThat(view.totalAmount()).isEqualByComparingTo("0");
        assertThat(view.totalQuantity()).isZero();
    }

    @Test
    @DisplayName("合计金额按行小计相加")
    void getCartTotals() {
        when(cartMapper.findByUsername("test")).thenReturn(cart());

        CartLineView l1 = new CartLineView();
        l1.setItemId("EST-1");
        l1.setItemPrice(new BigDecimal("9.90"));
        l1.setQuantity(2);

        CartLineView l2 = new CartLineView();
        l2.setItemId("EST-2");
        l2.setItemPrice(new BigDecimal("199.00"));
        l2.setQuantity(1);

        when(cartMapper.findLinesByCartId(7)).thenReturn(List.of(l1, l2));

        CartView view = cartService.getCart("test");

        assertThat(view.items()).hasSize(2);
        assertThat(view.totalQuantity()).isEqualTo(3);
        assertThat(view.totalAmount()).isEqualByComparingTo("218.80");
        assertThat(l1.getSubtotal()).isEqualByComparingTo("19.80");
    }
}
