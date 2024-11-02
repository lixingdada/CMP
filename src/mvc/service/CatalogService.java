
/*处理商品展示的业务逻辑*/

package mvc.service;
import mvc.domain.Item;
import mvc.domain.Product;
import mvc.persistence.CategoryDao;
import mvc.persistence.Impl.CategoryDaoImpl;
import mvc.persistence.Impl.ItemDaoImpl;
import mvc.persistence.Impl.ProductDaoImpl;
import mvc.persistence.ItemDao;
import mvc.persistence.ProductDao;

import java.util.List;

public class CatalogService {
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private ItemDao itemDao;

    public CatalogService(){
        categoryDao = new CategoryDaoImpl();
        productDao = new ProductDaoImpl();
        itemDao = new ItemDaoImpl();
    }

    /*获取商品中类列表*/
    public List<Product> getProductList(String categoryID){
        List<Product> list= productDao.getProductListByCategory(categoryID);
  /*      System.out.println(list.size());
        System.out.println(list.get(0).getProductId());
        System.out.println(list.get(0).getCategoryId());
        System.out.println(list.get(0).getName());
        System.out.println(list.get(0).getDescription());
        System.out.println(list);*/
        return list;
    }

    /*获取商品小类列表*/
    public List<Item> getItemList(String productID){
        List<Item> list= itemDao.getItemListByProduct(productID);
        return list;
    }

    public Item getItem(String itemID){
        return itemDao.getItem(itemID);
    }

    /*通过关键词检索*/
    public List<Product> getSearchProductList(String keyword){
        return productDao.searchProductList(keyword);
    }

}
