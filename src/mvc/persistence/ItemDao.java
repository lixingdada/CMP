package mvc.persistence;

import mvc.domain.Item;

import java.util.List;
import java.util.Map;

public interface ItemDao {
    void updateInventoryQuantity(Map<String, Object> param);

    int getInventoryQuantity(String itemId);

    /*通过中类商品id获得整个商品小类的列表*/
    List<Item> getItemListByProduct(String productId);

    /*通过小类商品id获得该商品的所有信息*/
    Item getItem(String itemId);

    String getUrlByProductId(String productId);

    List<Item> getItemsList();

    //通过itemid获得产品名字（数据库更新数据前）
    String getProductNameByItemId(String itemId);

    //通过itemid获得产品名字（数据库更新数据后）
    String getItemNameByItemId(String itemId);
}
