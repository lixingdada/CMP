
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
    <link rel="stylesheet" href="css/2.css">
</head>

<body>
<div>
<h1 class="centered-text">订单详情</h1>

<form class="centered-text">
<table class="centered-text">
    <tr class="centered-text">
        <th class="centered-text">商品ID</th>
        <th class="centered-text">数量</th>
        <th class="centered-text">价格</th>
    </tr>

    <c:forEach var="item" items="${sessionScope.orderItems}">
        <tr class="centered-text">
            <td class="centered-text">${item.itemId}</td>
            <td class="centered-text">${item.quantity}</td>
            <td class="centered-text">${item.price}</td>
        </tr>
    </c:forEach>
</table>
</form>

<div class="centered-text">
    <a href="myOrderForm?username=${sessionScope.username}" class="Button centered-text">我的订单</a>
</div>

</div>
</body>
</html>
<%@ include file="../common/bottom.jsp" %>

