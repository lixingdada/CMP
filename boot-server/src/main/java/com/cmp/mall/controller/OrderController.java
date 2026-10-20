package com.cmp.mall.controller;

import com.cmp.mall.aop.OperationLog;
import com.cmp.mall.common.Result;
import com.cmp.mall.dto.CreateOrderRequest;
import com.cmp.mall.dto.OrderView;
import com.cmp.mall.dto.PageResult;
import com.cmp.mall.security.UserContext;
import com.cmp.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单", description = "下单、订单列表、订单详情")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "下单（扣库存 + 建订单 + 清购物车 在同一事务内）")
    @OperationLog(action = "CREATE_ORDER")
    @PostMapping
    public Result<OrderView> create(@Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(orderService.create(UserContext.require(), request));
    }

    @Operation(summary = "我的订单（分页）")
    @GetMapping
    public Result<PageResult<OrderView>> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "5") int size) {
        return Result.ok(orderService.listOrders(UserContext.require(), page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderView> detail(@PathVariable Integer orderId) {
        return Result.ok(orderService.getOrder(UserContext.require(), orderId));
    }
}
