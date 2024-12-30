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
    private static  final String GET_QUANTITY = "  SELECT listprice FROM item WHERE itemid = ?";
    private static  final String GET_ITEM_LIST = "  SELECT * FROM item WHERE  productid= ?";
    private static  final String GET_ITEM = "  SELECT * FROM item WHERE itemid = ?";

    private static final String GET_URL_BY_PRODUCT_ID = "SELECT *from item where attr2 LIKE ?";

    private static final String GET_ITEMS_LIST = "SELECT *from item";

    private static final String GET_PRODUCT_NAME_BY_ITEM_ID ="SELECT p.name FROM item i JOIN product p ON i.productid = p.productid WHERE i.itemid = ?";


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

    @Override
    public void updateInventoryQuantity(Map<String, Object> param) {
    }

    //不知道要干嘛这个的函数
    @Override
    public int getInventoryQuantity(String itemId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(itemId);

        ResultSet resultSet = DBUtil.executeQuery(GET_QUANTITY,arrayList);
        try {
            if (resultSet.next()){

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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


}
