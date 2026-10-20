package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.PageResult;
import com.cmp.mall.dto.ProductDetailView;
import com.cmp.mall.entity.Category;
import com.cmp.mall.entity.Item;
import com.cmp.mall.entity.Product;
import com.cmp.mall.mapper.CategoryMapper;
import com.cmp.mall.mapper.ItemMapper;
import com.cmp.mall.mapper.ProductMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 商品目录：分类、商品、SKU 三级浏览 */
@Service
@RequiredArgsConstructor
public class CatalogService {

    private static final int MAX_PAGE_SIZE = 100;

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final ItemMapper itemMapper;

    public List<Category> listCategories() {
        return categoryMapper.findAll();
    }

    /**
     * 分页查询商品。
     *
     * <p>PageHelper 的实现是拦在 MyBatis 的 Executor 上：{@code startPage} 把分页参数放进
     * 当前线程的 ThreadLocal，下一次查询被拦截时自动改写 SQL 加上 LIMIT，并额外发一条
     * COUNT 查询。所以 {@code startPage} 之后必须紧跟目标查询，中间不能插入别的 SQL，
     * 否则分页参数会被错误地消费掉。
     */
    @Transactional(readOnly = true)
    public PageResult<Product> listProducts(String categoryId, String keyword, int pageNum, int pageSize) {
        int safePage = Math.max(pageNum, 1);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);

        PageHelper.startPage(safePage, safeSize);
        List<Product> list = productMapper.findByCategory(categoryId, keyword);
        PageInfo<Product> info = new PageInfo<>(list);
        return PageResult.of(info.getTotal(), info.getPageNum(), info.getPageSize(), info.getList());
    }

    @Transactional(readOnly = true)
    public ProductDetailView getProductDetail(String productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BizException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        List<Item> items = itemMapper.findByProductId(productId);
        return new ProductDetailView(product, items);
    }

    public Item getItem(String itemId) {
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BizException(ErrorCode.ITEM_NOT_FOUND);
        }
        return item;
    }
}
