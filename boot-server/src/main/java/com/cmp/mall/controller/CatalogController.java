package com.cmp.mall.controller;

import com.cmp.mall.common.Result;
import com.cmp.mall.dto.PageResult;
import com.cmp.mall.dto.ProductDetailView;
import com.cmp.mall.entity.Category;
import com.cmp.mall.entity.Item;
import com.cmp.mall.entity.Product;
import com.cmp.mall.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品浏览。这些接口不需要登录——未登录用户也能逛商品，加购时才要求身份。
 */
@Tag(name = "商品目录", description = "分类、商品、SKU 三级浏览")
@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @Operation(summary = "分类列表")
    @GetMapping("/api/categories")
    public Result<List<Category>> categories() {
        return Result.ok(catalogService.listCategories());
    }

    @Operation(summary = "商品分页列表，支持按分类过滤与关键字搜索")
    @GetMapping("/api/products")
    public Result<PageResult<Product>> products(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.ok(catalogService.listProducts(categoryId, keyword, page, size));
    }

    @Operation(summary = "商品详情（含其下所有 SKU）")
    @GetMapping("/api/products/{productId}")
    public Result<ProductDetailView> productDetail(@PathVariable String productId) {
        return Result.ok(catalogService.getProductDetail(productId));
    }

    @Operation(summary = "单个 SKU")
    @GetMapping("/api/items/{itemId}")
    public Result<Item> item(@PathVariable String itemId) {
        return Result.ok(catalogService.getItem(itemId));
    }
}
