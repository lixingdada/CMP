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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** 购物车 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;

    @Transactional(readOnly = true)
    public CartView getCart(String username) {
        Cart cart = cartMapper.findByUsername(username);
        if (cart == null) {
            // 历史用户可能没有 cart 记录，这里不写库，直接返回空车
            return new CartView(null, List.of(), 0, BigDecimal.ZERO);
        }

        List<CartLineView> lines = cartMapper.findLinesByCartId(cart.getCartId());
        BigDecimal total = BigDecimal.ZERO;
        int totalQuantity = 0;
        for (CartLineView line : lines) {
            BigDecimal subtotal = line.getItemPrice()
                    .multiply(BigDecimal.valueOf(line.getQuantity()));
            line.setSubtotal(subtotal);
            total = total.add(subtotal);
            totalQuantity += line.getQuantity();
        }
        return new CartView(cart.getCartId(), lines, totalQuantity, total);
    }

    /**
     * 加入购物车。同一商品再次加购要累加数量，不能插新行。
     *
     * <p>定位键先用 item_id，取不到退回 item_name。老数据只写了 item_name，
     * item_id 是 NULL，只按 item_id 找会永远找不到，结果每次都插一行，数量永远不累加。
     */
    @Transactional(rollbackFor = Exception.class)
    public void addItem(String username, String itemId, int quantity) {
        if (quantity <= 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "数量必须大于 0");
        }
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(ErrorCode.ITEM_NOT_FOUND);
        }

        Integer cartId = getOrCreateCartId(username);

        CartItem existing = cartMapper.findItemByCartAndItemId(cartId, itemId);
        if (existing == null) {
            // 第二代数据：item_name 写的是展示名
            existing = cartMapper.findItemByCartAndName(cartId, buildItemName(item));
        }
        if (existing == null) {
            // 第一代数据：item_name 写的是 itemId，item_id 反而是 NULL
            existing = cartMapper.findItemByCartAndName(cartId, item.getItemId());
        }

        int targetQuantity = quantity + (existing == null ? 0 : existing.getQuantity());
        if (item.getQty() != null && targetQuantity > item.getQty()) {
            throw new BizException(ErrorCode.OUT_OF_STOCK,
                    "库存不足，当前仅剩 " + item.getQty() + " 件");
        }

        if (existing == null) {
            CartItem line = new CartItem();
            line.setCartId(cartId);
            line.setItemId(item.getItemId());
            line.setItemName(buildItemName(item));
            line.setItemPrice(item.getListPrice());
            line.setQuantity(quantity);
            cartMapper.insertItem(line);
        } else {
            cartMapper.updateItemQuantity(cartId, existing.getItemName(), targetQuantity);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(String username, String itemId, int quantity) {
        if (quantity <= 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "数量必须大于 0");
        }
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(ErrorCode.ITEM_NOT_FOUND);
        }
        if (item.getQty() != null && quantity > item.getQty()) {
            throw new BizException(ErrorCode.OUT_OF_STOCK,
                    "库存不足，当前仅剩 " + item.getQty() + " 件");
        }

        Cart cart = cartMapper.findByUsername(username);
        if (cart == null) {
            throw new BizException(ErrorCode.CART_EMPTY);
        }
        int affected = cartMapper.updateItemQuantityById(cart.getCartId(), itemId, quantity);
        if (affected == 0) {
            // 老数据 item_id 为空，退回按名称更新
            CartItem byName = cartMapper.findItemByCartAndName(cart.getCartId(), buildItemName(item));
            if (byName == null) {
                throw new BizException(ErrorCode.ITEM_NOT_FOUND, "购物车中没有该商品");
            }
            cartMapper.updateItemQuantity(cart.getCartId(), byName.getItemName(), quantity);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeItem(String username, String itemId) {
        Cart cart = cartMapper.findByUsername(username);
        if (cart == null) {
            return;
        }
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(ErrorCode.ITEM_NOT_FOUND);
        }
        int affected = cartMapper.deleteItem(cart.getCartId(), buildItemName(item));
        if (affected == 0) {
            CartItem byId = cartMapper.findItemByCartAndItemId(cart.getCartId(), itemId);
            if (byId != null) {
                cartMapper.deleteItem(cart.getCartId(), byId.getItemName());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void clear(String username) {
        Cart cart = cartMapper.findByUsername(username);
        if (cart != null) {
            cartMapper.deleteByCartId(cart.getCartId());
        }
    }

    private Integer getOrCreateCartId(String username) {
        Cart cart = cartMapper.findByUsername(username);
        if (cart != null) {
            return cart.getCartId();
        }
        cartMapper.insertCart(username);
        Cart created = cartMapper.findByUsername(username);
        if (created == null) {
            throw new BizException(ErrorCode.DB_ERROR, "购物车创建失败");
        }
        return created.getCartId();
    }

    /**
     * 展示名。原库把"商品名 + 规格"的完整展示名放在 {@code item.attr3} 里
     * （如 "天使鱼 大型"），直接沿用，保证购物车、订单明细、商品详情三处展示一致。
     */
    private String buildItemName(Item item) {
        if (item.getAttr3() != null && !item.getAttr3().isBlank()) {
            return item.getAttr3();
        }
        StringBuilder sb = new StringBuilder();
        if (item.getAttr1() != null && !item.getAttr1().isBlank()) {
            sb.append(item.getAttr1());
        }
        if (item.getAttr2() != null && !item.getAttr2().isBlank()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(item.getAttr2());
        }
        return sb.length() == 0 ? item.getItemId() : sb.toString();
    }
}
