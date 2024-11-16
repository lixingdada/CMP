
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/top.jsp"%>

<html>
<head>
    <title>Product</title>
    <link rel="stylesheet" type="text/css" href="css/product.css"> <!-- 引入样式文件 -->
</head>
<body>
这是${sessionScope.productName}的item

<form action="./main" method="post" class="search-form">
    <input type="text" name="information" placeholder="${requestScope.information}"/>

    <button type="submit" class="image-button">
        <img src="${pageContext.request.contextPath}/images/searchicon.png" alt="搜索" />
    </button>
</form>

<br>

</br>
<%--<table>
    <tr>
        <td>具体商品id</td>
        <td>商品id</td>
        <td>价格</td>
        <td>商家</td>
        <td>描述</td>
    </tr>--%>
<div class="item-container">
    <c:forEach items="${sessionScope.ItemList}" var="item">
      <%--  <tr>
            <td>${item.itemId}</td>
            <td>${item.productId}</td>
            <td>${item.listPrice}</td>
            <td>${item.supplierId}</td>
            <td>${item.attribute1}</td>
            <td><a href="./item?itemId=${item.itemId}">查看</a></td>
        </tr>--%>
        <div class="item-card">
            <img src="images/${item.attribute2}" alt="${sessionScope.productName}">
            <div class="item-details">
                <h3>商品ID: ${item.itemId}</h3>
                <p>价格: ¥${item.listPrice}</p>
                <p>商家: ${item.supplierId}</p>
                <p>描述: ${item.attribute1}</p>
                <form action="./item" method="get">
                    <input type="hidden" name="itemId" value="${item.itemId}">
                    <input type="hidden" name="username" value="${sessionScope.user.username}">
                    <button type="submit" class="view-button">查看</button>
                </form>
            </div>
        </div>
    </c:forEach>
</div>
<%--</table>--%>

<div class="back-link-container">
    <form action="category" method="post" class="return-button-form">
        <button type="submit" class="return-button">返回</button>
    </form>
</div>



</body>
</html>
