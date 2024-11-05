<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h1>订单详情</h1>

<table>
    <tr>
        <th>商品ID</th>
        <th>数量</th>
        <th>价格</th>
    </tr>
    <c:forEach var="item" items="${orderItems}">
        <tr>
            <td>${item.itemId}</td>
            <td>${item.quantity}</td>
            <td>${item.price}</td>
        </tr>
    </c:forEach>
</table>
<%@ include file="../common/bottom.jsp" %>

