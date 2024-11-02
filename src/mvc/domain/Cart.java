package mvc.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class Cart implements Serializable {
    private static final long serialVersionUID = 8329559983943337176L;

    //提高查询效率
    private final Map<String, CartItem> itemMap = Collections.synchronizedMap(new HashMap<String, CartItem>());

    //存储数据
    private final List<CartItem> itemList = new ArrayList<CartItem>();

    public Iterator<CartItem> getCartItems() {
        return itemList.iterator();
    }

    public List<CartItem> getCartItemList() {
        return itemList;
    }

    public int getNumberOfItems() {
        return itemList.size();
    }

    public Iterator<CartItem> getAllCartItems() {
        return itemList.iterator();
    }

    public boolean containsItemId(String itemId) {
        return itemMap.containsKey(itemId);
    }



    //添加购物车商品
    public void addItem(Item item) {
        CartItem cartItem = (CartItem) itemMap.get(item.getItemId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setQuantity(0);
            itemMap.put(item.getItemId(), cartItem);
            itemList.add(cartItem);
        }
        cartItem.incrementQuantity();
    }

    //通过ID移除购物车商品
    public Item removeItemById(String itemId) {
        CartItem cartItem = (CartItem) itemMap.remove(itemId);
        if (cartItem == null) {
            return null;
        } else {
            itemList.remove(cartItem);
            return cartItem.getItem();
        }
    }

    //增加购物车某商品数量
    public void incrementQuantityByItemId(String itemId) {
        CartItem cartItem = (CartItem) itemMap.get(itemId);
        cartItem.incrementQuantity();
    }

    //设置购物车某商品数量
    public void setQuantityByItemId(String itemId, int quantity) {
        CartItem cartItem = (CartItem) itemMap.get(itemId);
        cartItem.setQuantity(quantity);
    }

    //计算总价
    public BigDecimal getSubTotal() {
        BigDecimal subTotal = new BigDecimal("0");
        Iterator<CartItem> items = getAllCartItems();
        while (items.hasNext()) {
            CartItem cartItem = (CartItem) items.next();
            Item item = cartItem.getItem();
            BigDecimal listPrice = item.getListPrice();
            BigDecimal quantity = new BigDecimal(String.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(listPrice.multiply(quantity));
        }
        return subTotal;
    }


}
