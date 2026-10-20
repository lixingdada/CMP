<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>购物车</title>
    <link rel="stylesheet" href="css/cart.css">
</head>

<body>
<div id="Cart">
    <h2>${sessionScope.username}的购物车</h2>
    <span style="display: none" id="username">${sessionScope.username}</span>

    <div class="cart-container">
        <table class="cart-item">
            <thead class="cart-header">
            <tr>
                <th>图片</th>
                <th>名称</th>
                <th>数量</th>
                <th>单价</th>
                <th>总价</th>
                <th>移除购物车</th>
            </tr>
            </thead>

            <tbody>
            <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart == null}">
                <tr>
                    <td id="emptyCartMessage" colspan="6" class="emptyCartMessage">您的购物车空空如也</td>
                </tr>
            </c:if>
            <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                <c:forEach var="cartItem" items="${sessionScope.cart.getAllCartItems()}">
                    <c:set var="currentItem" value="${sessionScope.catalogService.getItem(cartItem.item.itemId)}"/>

                    <tr class="product-details">
                        <td style="display: none" id="itemId">${cartItem.item.itemId}</td>
                        <td class="product-image">
                            <img src="images/${currentItem.getAttribute2()}" alt="">
                        </td>
                        <td class="product-name">
                            <a href="item?itemId=${cartItem.item.itemId}&username=${sessionScope.username}" id="itemName">${currentItem.getAttribute3()}</a>
                        </td>
                        <td class="product-quantity">
                            <div class="quantity-wrapper">
                                <input class="quantity-input" type="number" id="quantity" name="${cartItem.item.itemId}" value="${cartItem.quantity}" min="0" step="1">
                            </div>
                        </td>
                        <td class="product-price" id="itemPrice">${cartItem.item.listPrice}</td>
                        <td class="product-total item-total" id="itemTotal">${cartItem.total}</td>
                        <td class="product-remove">
                            <button id="remove" value="${cartItem.item.itemId}" class="Button">移除</button>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>

            <tfoot class="bottom-part">
            <tr class="bottom">
                <td class="total-label">
                    合计：
                </td>
                <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart == null}">
                    <td class="total-price">
                        0.00
                    </td>
                </c:if>
                <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                    <td class="total-price" id="cartTotal">
                            ${sessionScope.cart.getSubTotal()}
                    </td>
                </c:if>
                <td class="text-container">
                    <a class="continue-buy" href="mainForm?username=${sessionScope.username}">继续购物</a>
                </td>
                <td class="text-container">
                    <a class="submit-order" href="confirmOrderForm?username=${sessionScope.username}">提交订单</a>
                </td>
            </tr>
            </tfoot>
        </table>

    </div>
</div>
</body>
<script src="js/cart.js"></script>
</html>
