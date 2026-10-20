package mvc.persistence.Impl;

import mvc.cache.CatalogCache;
import mvc.domain.Category;
import mvc.persistence.CategoryDao;
import mvc.persistence.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类 DAO。
 *
 * <p>在原有实现上接入 {@link CatalogCache}：分类属于"读多写极少"的数据，
 * 首页和导航栏每次请求都会查，加一层进程内缓存可以挡掉绝大部分重复查询。
 * 命中时直接返回，不再访问数据库。
 */
public class CategoryDaoImpl implements CategoryDao {

    private static final String GET_CATEGORY = "SELECT * FROM  category WHERE catid = ?";
    private static final String GET_C_LIST = "SELECT * FROM  category ORDER BY catid";

    private Category resultSetToCategory(ResultSet resultSet) {
        Category category = new Category();
        try {
            category.setCategoryId(resultSet.getString("catid"));
            category.setName(resultSet.getString("name"));
            category.setDescription(resultSet.getString("descn"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return category;
    }

    @Override
    public List<Category> getCategoryList() {
        List<Category> cached = CatalogCache.getCategoryList();
        if (cached != null) {
            return cached;
        }

        List<Category> list = new ArrayList<Category>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(GET_C_LIST);
            while (resultSet.next()) {
                list.add(resultSetToCategory(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closeStatement(statement);
            DBUtil.closeConnection(connection);
        }

        CatalogCache.putCategoryList(list);
        return list;
    }

    @Override
    public Category getCategory(String categoryId) {
        Category cached = CatalogCache.getCategory(categoryId);
        if (cached != null) {
            return cached;
        }

        ArrayList<Object> params = new ArrayList<Object>();
        params.add(categoryId);

        ResultSet resultSet = null;
        try {
            resultSet = DBUtil.executeQuery(GET_CATEGORY, params);
            Category category = null;
            if (resultSet.next()) {
                category = resultSetToCategory(resultSet);
                CatalogCache.putCategory(category);
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.close();
        }
    }
}
