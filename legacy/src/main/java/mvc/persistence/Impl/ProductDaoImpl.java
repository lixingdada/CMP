package mvc.persistence.Impl;

import mvc.domain.Product;
import mvc.persistence.DBUtil;
import mvc.persistence.ProductDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    private  static final String GET_P_LIST_FROM_C = " SELECT *FROM product WHERE CATEGORY =?";
    private  static final String  GET_PRODUCT= "  SELECT *FROM PRODUCT WHERE productid =?";
    private  static final String  SEARCH_PRODUCT_LIST= "    SELECT * FROM product WHERE name LIKE ? OR descn LIKE ?";

    private Product resultSetToProduct(ResultSet resultSet) {
        Product product = new Product();
        try {
            product.setProductId(resultSet.getString("productid"));
            product.setCategoryId(resultSet.getString("category"));
            product.setName(resultSet.getString("name"));
            product.setDescription(resultSet.getString("descn"));
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return product;
    }
    @Override
    public List<Product> getProductListByCategory(String categoryId) {
        List<Product> list = new ArrayList<>();
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(categoryId);

        try {
            ResultSet resultSet =  DBUtil.executeQuery(GET_P_LIST_FROM_C,arrayList);
            while(resultSet.next())
            {
                Product product = resultSetToProduct(resultSet);
                list.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DBUtil.close();
        return list;
    }

    @Override
    public Product getProduct(String productId) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(productId);
        ResultSet resultSet = DBUtil.executeQuery(GET_PRODUCT,arrayList);
        try {
            Product product = null;
            if(resultSet.next())
            {
                product= resultSetToProduct(resultSet);
            }
            DBUtil.close();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Product> searchProductList(String keywords) {

            List<Product> list = new ArrayList<>();
            ArrayList<Object> arrayList  = new ArrayList<>();
            arrayList.add("%"+keywords+"%");
            arrayList.add("%"+keywords+"%");

            try {

                ResultSet resultSet = DBUtil.executeQuery(SEARCH_PRODUCT_LIST,arrayList);
                while(resultSet.next())
                {
                    Product product = resultSetToProduct(resultSet);
                    list.add(product);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            DBUtil.close();
            return list;
        }



    }

