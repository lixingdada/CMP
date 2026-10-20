package mvc.cache;

import mvc.domain.Category;
import mvc.util.LRUCache;

import java.util.List;

/**
 * 商品目录缓存。
 *
 * <p>分类页、导航栏每次请求都要查一遍，但数据几乎不改，很适合缓存。
 * 没上 Redis：部署是单机 Tomcat，分类就几十条记录，进程内缓存够用，
 * 多引一个中间件反而多一份运维成本。
 *
 * <p>整表一份，按 id 查又一份，都用 {@link LRUCache}。
 */
public final class CatalogCache {

    /** 分类总量很小，容量给 64 足够，超出说明有脏数据 */
    private static final LRUCache<String, Category> CATEGORY_BY_ID = new LRUCache<String, Category>(64);

    private static volatile List<Category> categoryList;

    private CatalogCache() {
    }

    public static List<Category> getCategoryList() {
        return categoryList;
    }

    public static void putCategoryList(List<Category> list) {
        categoryList = list;
        if (list != null) {
            for (Category category : list) {
                if (category != null && category.getCategoryId() != null) {
                    CATEGORY_BY_ID.put(category.getCategoryId(), category);
                }
            }
        }
    }

    public static Category getCategory(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        return CATEGORY_BY_ID.get(categoryId);
    }

    public static void putCategory(Category category) {
        if (category != null && category.getCategoryId() != null) {
            CATEGORY_BY_ID.put(category.getCategoryId(), category);
        }
    }

    /** 分类被新增/修改/删除后调用，避免读到旧数据 */
    public static void invalidate() {
        categoryList = null;
        CATEGORY_BY_ID.clear();
    }

    public static String stats() {
        return "CatalogCache{" + (categoryList == null ? "list=null" : "list=" + categoryList.size())
                + ", byId=" + CATEGORY_BY_ID.stats() + "}";
    }
}
