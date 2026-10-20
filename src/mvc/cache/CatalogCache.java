package mvc.cache;

import mvc.domain.Category;
import mvc.util.LRUCache;

import java.util.List;

/**
 * 商品目录缓存。
 *
 * <p>为什么选分类做缓存：分类数据是典型的"读多写极少"——首页、导航栏、商品编辑页
 * 每次请求都要查一遍，但它一年可能都不改一次，放在数据库里反复查是纯浪费。
 *
 * <p>为什么不用 Redis：这个项目的部署形态是单机 Tomcat，引入外部中间件只会增加运维成本；
 * 而缓存对象是几十条分类记录，体量极小，进程内缓存足够。选型要匹配场景，不是越重越好。
 *
 * <p>内部用 {@link LRUCache} 做两级：整表列表一份，按 ID 查一份。
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
