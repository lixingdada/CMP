
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/top.jsp"%>

<html>
<head>
    <title>Product</title>
    <link rel="stylesheet" type="text/css" href="css/product.css"> <!-- 引入样式文件 -->
</head>
<body>

<p>
    <br>
    <br>
    <br>
</p>

<!-- 搜索表单 -->
<div id="search">
    <form action="./product" method="post" class="search-form">
        <div class="search-container">
            <input type="search" name="information" id="information" placeholder=${sessionScope.information}>
            <button type="submit" class="search-button">🔍</button>
        </div>
    </form>
    <div id = "productAutoComplete" >
        <ul id="productAutoList">
            <%--            <li class="productAutoItem">1111111111</li>--%>
            <%--            <li class="productAutoItem">2222222222222</li>--%>
            <%--            <li class="productAutoItem">3333333333333</li>--%>
        </ul>
    </div>
</div>

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
<%--    <c:forEach items="${sessionScope.ItemList}" var="item">--%>
<%--      &lt;%&ndash;  <tr>--%>
<%--            <td>${item.itemId}</td>--%>
<%--            <td>${item.productId}</td>--%>
<%--            <td>${item.listPrice}</td>--%>
<%--            <td>${item.supplierId}</td>--%>
<%--            <td>${item.attribute1}</td>--%>
<%--            <td><a href="./item?itemId=${item.itemId}">查看</a></td>--%>
<%--        </tr>&ndash;%&gt;--%>
<%--        <div class="item-card">--%>
<%--            <img src="images/${item.attribute2}" alt="${sessionScope.productName}">--%>
<%--            <div class="item-details">--%>
<%--                <h3>商品ID: ${item.itemId}</h3>--%>
<%--                <p>价格: ¥${item.listPrice}</p>--%>
<%--                <p>商家: ${item.supplierId}</p>--%>
<%--                <p>描述: ${item.attribute1}</p>--%>
<%--                <form action="./item" method="get">--%>
<%--                    <input type="hidden" name="itemId" value="${item.itemId}">--%>
<%--                    <input type="hidden" name="username" value="${sessionScope.user.username}">--%>
<%--                    <button type="submit" class="view-button">查看</button>--%>
<%--                </form>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </c:forEach>--%>

    <div class="category-container">
        <c:forEach items="${sessionScope.ItemList}" var="item">
            <a href="item?itemId=${item.itemId}&username=${sessionScope.user.username}" class="category-box">
                <img src="images/${item.attribute2}" alt="Python">
                <div className="category-text">${requestScope.productName}      ${item.listPrice}</div>
            </a>
        </c:forEach>
    </div>
</div>
<%--</table>--%>

<%--<div class="back-link-container">--%>
<%--    <form action="main" method="get" class="return-button-form">--%>
<%--        <button type="submit" class="return-button">返回</button>--%>
<%--    </form>--%>
<%--</div>--%>

<script src="js/product.js"></script>

</body>
</html>
