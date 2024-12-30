package mvc.persistence.Impl;

import mvc.domain.Category;
import mvc.persistence.CategoryDao;
import mvc.persistence.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    private  static final String GET_CATEGORY = "SELECT * FROM  category WHERE catid = ?";
    private  static final String GET_C_LIST= "SELECT * FROM  category ";

    private Category resultSetToCategory(ResultSet resultSet) {
       Category category = new Category();
        try {
            category.setCategoryId(resultSet.getString("catid"));
            category.setName(resultSet.getString("name"));
            category.setDescription(resultSet.getString("descn"));
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return category;
    }

    @Override
    public List<Category> getCategoryList() {
        List<Category> list = new ArrayList<>();

        try {
            Connection connection = DBUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_C_LIST);
            while(resultSet.next())
            {
                Category category = resultSetToCategory(resultSet);
                list.add(category);
            }
            DBUtil.closeStatement(statement);
            DBUtil.closeResultSet(resultSet);
            DBUtil.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Category getCategory(String categoryId) {

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(categoryId);
        ResultSet resultSet = DBUtil.executeQuery(GET_CATEGORY,arrayList);
        try {
            Category category = null;
            if(resultSet.next())
            {
                category= resultSetToCategory(resultSet);
            }
            DBUtil.close();
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
