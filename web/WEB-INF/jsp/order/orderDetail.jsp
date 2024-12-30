
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
    <link rel="stylesheet" href="css/orderdetail.css">
</head>

<body>
<div>
<h1>订单详情</h1>

    <div class="order-container">
        <table class="order-item">
            <thead class="order-header">
                <tr>
                    <th>商品ID</th>
                    <th>数量</th>
                    <th>单价</th>
                    <th>总价</th>
                </tr>
            </thead>

            <tbody>
    <c:forEach var="item" items="${sessionScope.orderItems}">
        <tr class="order-details">
            <td class="item-name">
                <a href="item?itemId=${item.itemId}&username=${sessionScope.username}" id="itemId">${item.itemId}</a>
            </td>
            <td class="item-quantity">${item.quantity}</td>
            <td class="item-price">${item.price}</td>
            <td class="item-total">${item.quantity * item.price}</td>
        </tr>
    </c:forEach>
    <tr class="total">
        <td colspan="4">
            合计：${sessionScope.totalPrice}
        </td>

    </tr>
            </tbody>

        <tfoot class="bottom-part">
        <tr>
            <td>
                <a href="myOrderForm?username=${sessionScope.username}" class="return">返回我的订单</a>
            </td>

        </tr>
        </tfoot>

</table>
    </div>




</div>
</body>
</html>


