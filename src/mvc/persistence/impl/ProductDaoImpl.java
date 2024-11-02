package mvc.persistence.impl;

import mvc.domain.Product;
import mvc.persistence.DBUtil;
import mvc.persistence.ProductDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    private static final String getProduct=
            "SELECT PRODUCTID, NAME, DESCN as description, CATEGORY as categoryId FROM PRODUCT WHERE PRODUCTID = ?";

    private static final String getProductListByCategory=
            "SELECT PRODUCTID, NAME, DESCN as description, CATEGORY as categoryId FROM PRODUCT WHERE CATEGORY =?";

    private static final String searchProductList=
            "SELECT PRODUCTID, NAME, DESCN as description, CATEGORY as categoryId from PRODUCT WHERE lower(name) like ?";

    @Override
    public List<Product> getProductListByCategory(String categoryId) {
        List<Product> products=new ArrayList<>();
        try{
            Connection connection= DBUtil.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(getProductListByCategory);
            preparedStatement.setString(1,categoryId);
            ResultSet resultSet=preparedStatement.executeQuery();

            while(resultSet.next()){
                Product product=new Product();
                product.setProductId(resultSet.getString(1));
                product.setName(resultSet.getString(2));
                product.setDescription(resultSet.getString(3));
                product.setCategoryId(resultSet.getString(4));
                products.add(product);
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public Product getProduct(String productId) {
        Product product=null;
        try{
            Connection connection= DBUtil.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(getProduct);
            preparedStatement.setString(1,productId);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                product=new Product();
                product.setProductId(resultSet.getString(1));
                product.setName(resultSet.getString(2));
                product.setDescription(resultSet.getString(3));
                product.setCategoryId(resultSet.getString(4));
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public List<Product> searchProductList(String keywords) {
        List<Product> productList=new ArrayList<>();
        try{
            Connection connection= DBUtil.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(searchProductList);
            preparedStatement.setString(1,keywords);
            ResultSet resultSet=preparedStatement.executeQuery();

            while(resultSet.next()){
                Product product=new Product();
                product.setProductId(resultSet.getString(1));
                product.setName(resultSet.getString(2));
                product.setDescription(resultSet.getString(3));
                product.setCategoryId(resultSet.getString(4));
                productList.add(product);
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return productList;
    }
}
