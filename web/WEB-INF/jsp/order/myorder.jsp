
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>我的订单</title>
    <link rel="stylesheet" href="css/2.css">
</head>


<body>
<div>
    <h2 class="centered-text">我的订单</h2>
    <form action="myOrderForm?username=${sessionScope.username}" method="post" class="centered-text">
        <table class="centered-text">
            <tr class="centered-text">
                <th class="centered-text">订单ID</th>
                <th class="centered-text">订单时间</th>
                <th class="centered-text">收件人</th>
                <th class="centered-text">收货地址</th>
                <th class="centered-text">联系电话</th>
            </tr>

            <c:forEach var="order" items="${sessionScope.myOrder}">
                <tr class="centered-text">
                    <td>
                        <a href="orderDetailForm?orderId=${order.orderId}&username=${sessionScope.username}" class="centered-text">${order.orderId}</a>
                    </td>

                    <td class="centered-text">
                        <fmt:formatDate value="${order.orderDate}" pattern="yyyy/MM/dd hh:mm:ss" />
                    </td>

                    <td class="centered-text">${order.orderName}</td>

                    <td class="centered-text">${order.orderAddress}</td>

                    <td class="centered-text">${order.orderTel}</td>
                </tr>
            </c:forEach>
        </table>
    </form>

    <div class="centered-text">
        <a href="mainForm?username=${sessionScope.username}" class="Button centered-text">返回首页</a>
    </div>
</div>
</body>
</html>
<%@ include file="../common/bottom.jsp" %>
