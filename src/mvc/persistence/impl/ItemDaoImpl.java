package mvc.persistence.impl;

import mvc.domain.Item;
import mvc.domain.Product;
import mvc.persistence.DBUtil;
import mvc.persistence.ItemDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class ItemDaoImpl implements ItemDao {

    private static final String getItemListByProductString =
            "SELECT I.ITEMID, LISTPRICE, UNITCOST, SUPPLIER AS supplierId, I.PRODUCTID AS \"product.productId\", NAME AS \"product.name\", DESCN AS \"product.description\", CATEGORY AS \"product.categoryId\", STATUS, ATTR1 AS attribute1, ATTR2 AS attribute2, ATTR3 AS attribute3, ATTR4 AS attribute4, ATTR5 AS attribute5 FROM ITEM I, PRODUCT P WHERE P.PRODUCTID = I.PRODUCTID AND I.PRODUCTID = ?";

    private static final String getItemString=
            "select I.ITEMID, LISTPRICE, UNITCOST, SUPPLIER AS supplierId, I.PRODUCTID AS \"product.productId\", NAME AS \"product.name\", DESCN AS \"product.description\", CATEGORY AS \"product.categoryId\", STATUS, ATTR1 AS attribute1, ATTR2 AS attribute2, ATTR3 AS attribute3, ATTR4 AS attribute4, ATTR5 AS attribute5, QTY AS quantity from ITEM I, INVENTORY V, PRODUCT P where P.PRODUCTID = I.PRODUCTID and I.ITEMID = V.ITEMID and I.ITEMID = ?";

    private static final String getInventoryQuantityString=
            "SELECT QTY AS value FROM INVENTORY WHERE ITEMID = ?";

    private static final String updateInventoryQuantityString=
            "UPDATE INVENTORY SET QTY = QTY - ? WHERE ITEMID = ?";

    @Override
    public void updateInventoryQuantity(Map<String, Object> param) {
        try{
            Connection connection= DBUtil.getConnection();
            PreparedStatement preparedStatement= connection.prepareStatement(updateInventoryQuantityString);
            String itemId =param.keySet().iterator().next();
            Integer increment=(Integer)param.get(itemId);
            preparedStatement.setInt(1,increment.intValue());
            preparedStatement.setString(2,itemId);
            preparedStatement.executeUpdate();
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getInventoryQuantity(String itemId) {
        int result=-1;
        try{
            Connection connection=DBUtil.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(getInventoryQuantityString);
            preparedStatement.setString(1,itemId);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                result=resultSet.getInt(1);
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Item> getItemListByProduct(String productId) {
        List<Item>  itemList=new ArrayList<>();
        try{
            Connection connection=DBUtil.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(getItemListByProductString);
            preparedStatement.setString(1,productId);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Item item=new Item();
                item.setItemId(resultSet.getString(1));
                item.setListPrice(resultSet.getBigDecimal(2));
                item.setUnitCost(resultSet.getBigDecimal(3));
                item.setSupplierId(resultSet.getInt(4));
                Product product=new Product();
                product.setProductId(resultSet.getString(5));
                product.setName(resultSet.getString(6));
                product.setDescription(resultSet.getString(7));
                product.setCategoryId(resultSet.getString(8));
                item.setProduct(product);
                item.setStatus(resultSet.getString(9));
                item.setAttribute1(resultSet.getString(10));
                item.setAttribute2(resultSet.getString(11));
                item.setAttribute3(resultSet.getString(12));
                item.setAttribute4(resultSet.getString(13));
                item.setAttribute5(resultSet.getString(14));
                itemList.add(item);
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
        return itemList;
    }

    @Override
    public Item getItem(String itemId) {
        Item item = null;
        try {
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getItemString);
            preparedStatement.setString(1, itemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                item = new Item();
                item.setItemId(resultSet.getString(1));
                item.setListPrice(resultSet.getBigDecimal(2));
                item.setUnitCost(resultSet.getBigDecimal(3));
                item.setSupplierId(resultSet.getInt(4));

                Product product = new Product();
                product.setProductId(resultSet.getString(5));
                product.setName(resultSet.getString(6));
                product.setDescription(resultSet.getString(7));
                product.setCategoryId(resultSet.getString(8));
                item.setProduct(product);

                item.setStatus(resultSet.getString(9));
                item.setAttribute1(resultSet.getString(10));
                item.setAttribute2(resultSet.getString(11));
                item.setAttribute3(resultSet.getString(12));
                item.setAttribute4(resultSet.getString(13));
                item.setAttribute5(resultSet.getString(14));
                item.setQuantity(resultSet.getInt(15)); // Assuming the quantity is the 15th column
            }

            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
