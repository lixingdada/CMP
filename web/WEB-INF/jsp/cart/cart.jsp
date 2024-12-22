
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>购物车</title>
    <link rel="stylesheet" href="css/2.css">
</head>

<body>

    <div id="BackLink">
        <a href="mainForm?username=${sessionScope.username}"> 返回主菜单</a>
    </div>

    <div id="cart">
        <h2 class="centered-text">${sessionScope.username}的购物车</h2>
        <form action="updateCart" method="post" class="centered-text">
            <span class="hidden" id="username">${sessionScope.username}</span>
            <table class="centered-text">
                <tr>
                    <th class="centered-text"><b>名称</b></th>
                    <th class="centered-text"><b>数量</b></th>
                    <th class="centered-text"><b>单价</b></th>
                    <th class="centered-text"><b>总价</b></th>
                    <th class="centered-text"><b>移出购物车</b></th>
                </tr>

                <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart ==null}">
                    <tr>
                        <td colspan="5" class="centered-text">您的购物车空空如也</td>
                    </tr>
                </c:if>

                <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                <c:forEach var="cartItem" items="${sessionScope.cart.cartItems}">
                    <tr>
                        <td class="centered-text">
                            <a href="itemForm?itemId=${cartItem.item.itemId}" id="itemId">${cartItem.item.itemId}</a>
                        </td>

                        <td class="centered-text">
                            <input class="text2" type="number" id="quantity" name="${cartItem.item.itemId}" value="${cartItem.quantity}" min="0" step="1">
                        </td>

                        <td class="centered-text" id="itemPrice">${cartItem.item.listPrice}</td>

                        <td class="centered-text" id="itemTotal">${cartItem.total}</td>

                        <td class="centered-text">
                            <button id="remove" value="${cartItem.item.itemId}" class="Button">移除</button>
                        </td>
                    </tr>
                </c:forEach>
                </c:if>

                <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart ==null}">
                    <tr>
                        <td class="centered-text">
                            合计：
                        </td>
                        <td colspan="4" class="centered-text">
                            $0.00
                        </td>
                    </tr>
                </c:if>

                <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                <tr>
                    <td class="centered-text">
                        合计：
                    </td>
                    <td colspan="4" class="centered-text" id="cartTotal">
                        ${sessionScope.cart.getSubTotal()}
                    </td>
                </tr>
                </c:if>

                <tr>
                    <td colspan="3" class="centered-text">
                        <a href="mainForm?username=${sessionScope.username}" class="Button centered-text">继续购物</a>
                    </td>

                    <td colspan="2" class="centered-text">
                        <a href="confirmOrderForm?username=${sessionScope.username}" class="Button centered-text">提交订单</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</body>
<script src="js/cartnumber.js"></script>
<script src="js/cartremove.js"></script>
</html>
