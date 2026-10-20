package com.cmp.mall.controller;

import com.cmp.mall.aop.OperationLog;
import com.cmp.mall.common.Result;
import com.cmp.mall.dto.AddCartItemRequest;
import com.cmp.mall.dto.CartView;
import com.cmp.mall.dto.UpdateCartItemRequest;
import com.cmp.mall.security.UserContext;
import com.cmp.mall.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "购物车")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "查看购物车")
    @GetMapping
    public Result<CartView> getCart() {
        return Result.ok(cartService.getCart(UserContext.require()));
    }

    @Operation(summary = "加入购物车")
    @OperationLog(action = "ADD_CART")
    @PostMapping("/items")
    public Result<Void> addItem(@Valid @RequestBody AddCartItemRequest request) {
        cartService.addItem(UserContext.require(), request.itemId(), request.quantity());
        return Result.ok();
    }

    @Operation(summary = "修改数量")
    @OperationLog(action = "UPDATE_CART")
    @PutMapping("/items/{itemId}")
    public Result<Void> updateQuantity(@PathVariable String itemId,
                                       @Valid @RequestBody UpdateCartItemRequest request) {
        cartService.updateQuantity(UserContext.require(), itemId, request.quantity());
        return Result.ok();
    }

    @Operation(summary = "移除条目")
    @OperationLog(action = "REMOVE_CART")
    @DeleteMapping("/items/{itemId}")
    public Result<Void> removeItem(@PathVariable String itemId) {
        cartService.removeItem(UserContext.require(), itemId);
        return Result.ok();
    }

    @Operation(summary = "清空购物车")
    @OperationLog(action = "CLEAR_CART")
    @DeleteMapping
    public Result<Void> clear() {
        cartService.clear(UserContext.require());
        return Result.ok();
    }
}
