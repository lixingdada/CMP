package mvc.persistence;

import mvc.domain.Category;

import java.util.List;

public interface CategoryDao {

    /*直接获取整个商品大类大列表*/
    List<Category> getCategoryList();

    /*通过categoryID获得对应的某一行商品大类*/
    Category getCategory(String categoryId);

}

