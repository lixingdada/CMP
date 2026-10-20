package mvc.persistence.Impl;

import mvc.domain.Category;
import mvc.domain.Item;
import mvc.persistence.DBUtil;
import mvc.persistence.ItemDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemDaoImpl implements ItemDao {
    private static  final String GET_QUANTITY = "  SELECT qty FROM item WHERE itemid = ?";
    private static  final String UPDATE_QUANTITY = "  UPDATE item SET qty = qty - ? WHERE itemid = ? AND qty >= ?";
    private static  final String GET_ITEM_LIST = "  SELECT * FROM item WHERE  productid= ?";
    private static  final String GET_ITEM = "  SELECT * FROM item WHERE itemid = ?";

    private static final String GET_URL_BY_PRODUCT_ID = "SELECT *from item where attr2 LIKE ?";

    private static final String GET_ITEMS_LIST = "SELECT *from item";

    private static final String GET_PRODUCT_NAME_BY_ITEM_ID ="SELECT p.name FROM item i JOIN product p ON i.productid = p.productid WHERE i.itemid = ?";

    private static final String GET_ITEM_NAME_BY_ITEM_ID = "SELECT attr3 FROM item WHERE itemid = ?";

    private Item resultSetToItem(ResultSet resultSet) {
        Item item = new Item();
        try {
            item.setItemId(resultSet.getString("itemid"));
            item.setProductId(resultSet.getString("productid"));
            item.setListPrice(resultSet.getBigDecimal("listprice"));
            item.setUnitCost(resultSet.getBigDecimal("unitcost"));
            item.setSupplierId(resultSet.getInt("supplier"));
            item.setStatus(resultSet.getString("status"));
            item.setAttribute1(resultSet.getString("attr1"));
            item.setAttribute2(resultSet.getString("attr2"));
            item.setAttribute3(resultSet.getString("attr3"));
            item.setAttribute4(resultSet.getString("attr4"));
            item.setAttribute5(resultSet.getString("attr5"));
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return item;
    }

    /**
     * 扣减库存。
     *
     * <p>参数沿用接口原有的 Map 形式，约定 key：{@code itemid}（或 {@code itemId}）
     * 指定条目，{@code increment}（或 {@code qty}）指定扣减数量，缺省按 1 处理。
     * SQL 里带 {@code qty >= ?} 条件，库存不足时更新 0 行，不会扣成负数。
     */
    @Override
    public void updateInventoryQuantity(Map<String, Object> param) {
        if (param == null) {
            return;
        }
        Object itemId = param.containsKey("itemid") ? param.get("itemid") : param.get("itemId");
        Object increment = param.containsKey("increment") ? param.get("increment") : param.get("qty");
        if (itemId == null) {
            return;
        }
        int decrement = increment instanceof Number ? ((Number) increment).intValue() : 1;
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(Integer.valueOf(decrement));
        arrayList.add(itemId.toString());
        arrayList.add(Integer.valueOf(decrement));
        DBUtil.executeUpdate(UPDATE_QUANTITY, arrayList);
    }

    /**
     * 查询条目库存。
     *
     * <p>原实现是空方法、恒返回 0，而 {@code CatalogService} 用
     * {@code getInventoryQuantity(id) > 0} 判断商品是否有货，于是详情页永远显示无货。
     */
    @Override
    public int getInventoryQuantity(String itemId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(itemId);

        ResultSet resultSet = null;
        try {
            resultSet = DBUtil.executeQuery(GET_QUANTITY, arrayList);
            if (resultSet.next()) {
                return resultSet.getInt("qty");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeResultSet(resultSet);
        }
        return 0;
    }

    @Override
    public List<Item> getItemListByProduct(String productId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(productId);
        List<Item> list = new ArrayList<>();

        ResultSet resultSet = DBUtil.executeQuery(GET_ITEM_LIST,arrayList);
        while (true){
            try {
                if (!resultSet.next()) break;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            list.add(resultSetToItem(resultSet));
        }
        DBUtil.close();
        return list;
    }

    @Override
    public Item getItem(String itemId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(itemId);
        ResultSet resultSet = DBUtil.executeQuery(GET_ITEM,arrayList);
        Item item = null;

        try {
            if (resultSet.next()){
                item = resultSetToItem(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DBUtil.close();
        return item;
    }

    @Override
    public String getUrlByProductId(String productId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        String s = productId.charAt(0)+productId.substring(2);
        String url =  "";
        arrayList.add("%"+s+"%");
        ResultSet resultSet = DBUtil.executeQuery(GET_URL_BY_PRODUCT_ID,arrayList);
        try {
            if (resultSet.next()){
                url = resultSet.getString("attr2");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DBUtil.close();
        return url;
    }

    @Override
    public List<Item> getItemsList() {
        List<Item> list = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ITEMS_LIST)) {

            while (resultSet.next()) {
                Item item = resultSetToItem(resultSet);
                list.add(item);
            }
            DBUtil.closeStatement(statement);
            DBUtil.closeResultSet(resultSet);
            DBUtil.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();  // 打印出异常堆栈信息
            throw new RuntimeException("Error retrieving items", e);  // 重新抛出异常以便调用者知道发生了问题
        }
        return list;
    }

    @Override
    public String getProductNameByItemId(String itemId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(itemId);
        ResultSet resultSet = DBUtil.executeQuery(GET_PRODUCT_NAME_BY_ITEM_ID,arrayList);
        String productName = "";
        try {
            if(resultSet.next()){
                productName = resultSet.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productName;
    }

    @Override
    public String getItemNameByItemId(String itemId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(itemId);
        ResultSet resultSet = DBUtil.executeQuery(GET_ITEM_NAME_BY_ITEM_ID,arrayList);
        String productName = "";
        try {
            if(resultSet.next()){
                productName = resultSet.getString("attr3");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productName;
    }


}
