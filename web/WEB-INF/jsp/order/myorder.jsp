
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>我的订单</title>
    <link rel="stylesheet" href="css/myorder.css">
</head>

<body>
<div>
    <h1>我的订单</h1>
    <form action="myOrderForm?username=${sessionScope.username}" method="post">
        <div class="order-container">
        <table class="order-item">
            <thead class="order-header">
            <tr>
                <th>订单ID</th>
                <th>订单时间</th>
                <th>收件人</th>
                <th>收货地址</th>
                <th>联系电话</th>
            </tr>
            </thead>

            <tbody>
            <c:set var="counter" value="0" scope="page"/>
            <c:forEach var="order" items="${sessionScope.myOrder}">
                <c:set var="counter" value="${counter + 1}" scope="page"/>
                <tr class="order-details">
                    <td class="order-id">
                        <a href="orderDetailForm?orderId=${order.orderId}&username=${sessionScope.username}" >${counter}</a>
                    </td>

                    <td class="order-time">
                        <fmt:formatDate value="${order.orderDate}" pattern="yyyy/MM/dd hh:mm:ss" />
                    </td>

                    <td class="order-name">
                            ${order.orderName}
                    </td>

                    <td class="order-address">
                            ${order.orderAddress}
                    </td>

                    <td class="order-tel">
                            ${order.orderTel}
                    </td>
                </tr>
            </c:forEach>
            </tbody>

            <tfoot class="bottom-part">
            <tr>
                <td>
                    <a href="mainForm?username=${sessionScope.username}" class="return">返回首页</a>
                </td>
            </tr>
            </tfoot>
        </table>
        </div>
    </form>


</div>
</body>
</html>

