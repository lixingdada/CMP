/*处理商品展示的业务逻辑*/
package mvc.service;

import mvc.domain.Category;
import mvc.domain.Item;
import mvc.domain.Product;
import mvc.persistence.CategoryDao;
import mvc.persistence.ItemDao;
import mvc.persistence.ProductDao;
import mvc.persistence.Impl.CategoryDaoImpl;
import mvc.persistence.Impl.ItemDaoImpl;
import mvc.persistence.Impl.ProductDaoImpl;

import java.util.List;

public class CatalogService {
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private ItemDao itemDao;

    public CatalogService(){
        this.categoryDao=new CategoryDaoImpl();
        this.itemDao=new ItemDaoImpl();
        this.productDao=new ProductDaoImpl();
    }

    public List<Category> getCategoryList() {
        return categoryDao.getCategoryList();
    }

    public Category getCategory(String categoryId) {
        return categoryDao.getCategory(categoryId);
    }

    public Product getProduct(String productId) {
        return productDao.getProduct(productId);
    }

    public List<Product> getProductListByCategory(String categoryId) {
        return productDao.getProductListByCategory(categoryId);
    }

    // TODO enable using more than one keyword
    public List<Product> searchProductList(String keyword) {
        return productDao.searchProductList("%" + keyword.toLowerCase() + "%");
    }

    public List<Item> getItemListByProduct(String productId) {
        return itemDao.getItemListByProduct(productId);
    }

    public Item getItem(String itemId) {
        return itemDao.getItem(itemId);
    }

    public boolean isItemInStock(String itemId) {
        return itemDao.getInventoryQuantity(itemId) > 0;
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


    /*通过关键词检索*/
    public List<Product> getSearchProductList(String keyword){
        return productDao.searchProductList(keyword);
    }

    public String getUrlByProductId(String productID){
        return itemDao.getUrlByProductId(productID);
    }

}
