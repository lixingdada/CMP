package com.cmp.mall.mapper;

import com.cmp.mall.entity.Cart;
import com.cmp.mall.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 购物车聚合：cart（车）+ cart_item（条目） */
@Mapper
public interface CartMapper {

    Cart findByUsername(@Param("username") String username);

    int insertCart(@Param("username") String username);

    /**
     * 查询某一项是否已在车内。原版按 item_id 定位，但加购时只写了 item_name，
     * item_id 恒为 NULL，导致重复加购数量永远不累加——这是改造前的一个真实缺陷，
     * 这里改用 (cart_id, item_name) 作为业务唯一标识。
     */
    CartItem findItemByCartAndName(@Param("cartId") Integer cartId,
                                   @Param("itemName") String itemName);

    CartItem findItemByCartAndItemId(@Param("cartId") Integer cartId,
                                     @Param("itemId") String itemId);

    List<CartItem> findByCartId(@Param("cartId") Integer cartId);

    /** 购物车行 + 实时库存，一次 JOIN 查回，避免 N+1 */
    List<com.cmp.mall.dto.CartLineView> findLinesByCartId(@Param("cartId") Integer cartId);

    int insertItem(CartItem item);

    int updateItemQuantity(@Param("cartId") Integer cartId,
                           @Param("itemName") String itemName,
                           @Param("quantity") Integer quantity);

    int updateItemQuantityById(@Param("cartId") Integer cartId,
                               @Param("itemId") String itemId,
                               @Param("quantity") Integer quantity);

    int deleteItem(@Param("cartId") Integer cartId, @Param("itemName") String itemName);

    int deleteByCartId(@Param("cartId") Integer cartId);

    int countItems(@Param("cartId") Integer cartId);
}
