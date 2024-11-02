package mvc.persistence;

import mvc.domain.Product;

import java.util.List;

public interface ProductDao {
    /*通过商品大类的id获得该商品大类下的所有商品中类列表*/
    List<Product> getProductListByCategory(String categoryId);

    /*通过商品中类id获取整个该商品中类的信息*/
    Product getProduct(String productId);

    /*通过检索关键词来找到商品中类列表*/
    List<Product> searchProductList(String keywords);
}
