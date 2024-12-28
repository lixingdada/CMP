
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>购物车</title>
    <link rel="stylesheet" href="css/2.css">
    <link rel="stylesheet" href="css/bottom.css"> <!-- 引入新的 bottom.css -->
</head>

<body>

<div id="BackLink">
    <a href="mainForm?username=exampleUser">返回主菜单</a>
</div>

<div id="Catalog">
    <div id="Cart">
        <h2 class="centered-text">购物车</h2>

        <form action="updateCart?username=exampleUser" method="post" class="centered-text">
            <!-- 商品信息：第一件商品 -->
            <div class="cart-item">
                <div class="product-image">
                    <img src="images/product1.png" alt="产品图"> <!-- 预留图片端口 -->
                </div>
                <div class="product-details">
                    <div class="product-name">
                        <a href="itemForm?itemId=12345">12345</a>
                    </div>
                    <div class="product-quantity">
                        <div class="quantity-wrapper">
                            <button type="button" class="quantity-button" onclick="changeQuantity(this, -1)">-</button>
                            <input type="number" class="quantity-input" name="12345" value="2" min="1" data-price="10" onchange="updateTotal(this)">
                            <button type="button" class="quantity-button" onclick="changeQuantity(this, 1)">+</button>
                        </div>
                    </div>
                    <div class="product-price">$10.00</div>
                    <div class="product-total item-total" data-item-id="12345">$20.00</div>
                    <div class="product-remove">
                        <a href="removeCartItem?workingItemId=12345&username=exampleUser" class="remove-link">移除</a>
                    </div>
                </div>
            </div>
            <hr class="separator"> <!-- 分割线 -->

            <!-- 商品信息：第二件商品 -->
            <div class="cart-item">
                <div class="product-image">
                    <img src="images/product2.png" alt="产品图"> <!-- 预留图片端口 -->
                </div>
                <div class="product-details">
                    <div class="product-name">
                        <a href="itemForm?itemId=67890">67890</a>
                    </div>
                    <div class="product-quantity">
                        <div class="quantity-wrapper">
                            <button type="button" class="quantity-button" onclick="changeQuantity(this, -1)">-</button>
                            <input type="number" class="quantity-input" name="67890" value="1" min="1" data-price="25" onchange="updateTotal(this)">
                            <button type="button" class="quantity-button" onclick="changeQuantity(this, 1)">+</button>
                        </div>
                    </div>
                    <div class="product-price">$25.00</div>
                    <div class="product-total item-total" data-item-id="67890">$25.00</div>
                    <div class="product-remove">
                        <a href="removeCartItem?workingItemId=67890&username=exampleUser" class="remove-link">移除</a>
                    </div>
                </div>
            </div>
            <hr class="separator"> <!-- 分割线 -->

            <!-- 合计 -->
            <div class="cart-total">
                合计: <span id="total-price">$45.00</span>
            </div>
        </form>
    </div>
</div>

<!-- 引入新的 buttom.jsp -->
<%@ include file="../common/bottom.jsp" %>

<script>
    // 调整数量按钮逻辑
    function changeQuantity(button, delta) {
        const input = button.parentElement.querySelector('.quantity-input');
        const min = parseInt(input.min) || 1; // 获取最小值（默认 1）
        const value = parseInt(input.value) || min; // 当前数量
        const newValue = value + delta;

        // 确保数量不小于最小值
        if (newValue >= min) {
            input.value = newValue;
            updateItemTotal(input); // 更新单个商品总价
            updateCartTotal(); // 更新购物车总价
        }
    }

    // 更新单个商品总价
    function updateItemTotal(input) {
        const price = parseFloat(input.dataset.price); // 获取商品单价
        const quantity = parseInt(input.value) || 0; // 当前数量
        const totalElement = input.closest('.cart-item').querySelector('.product-total'); // 找到总价显示区域
        const newTotal = price * quantity; // 计算新的总价

        // 更新单个商品的总价显示
        totalElement.textContent = `$${newTotal.toFixed(2)}`;
    }

    // 更新购物车合计
    function updateCartTotal() {
        let total = 0;

        // 遍历所有商品，计算购物车的总价
        document.querySelectorAll('.quantity-input').forEach(input => {
            const price = parseFloat(input.dataset.price); // 获取单价
            const quantity = parseInt(input.value) || 0; // 获取数量
            total += price * quantity; // 累加到总价
        });

        // 更新购物车总价显示
        document.getElementById('total-price').textContent = `$${total.toFixed(2)}`;
    }

</script><%--随便写的逻辑,后续可能需要改--%>

</body>
</html>
