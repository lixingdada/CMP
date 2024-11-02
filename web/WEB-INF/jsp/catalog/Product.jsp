
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Product</title>
</head>
<body>
这是${sessionScope.productName}的item
<table>
    <tr>
        <td>具体商品id</td>
        <td>商品id</td>
        <td>价格</td>
        <td>商家</td>
        <td>描述</td>
    </tr>
    <c:forEach items="${sessionScope.ItemList}" var="item">
        <tr>
            <td>${item.itemId}</td>
            <td>${item.productId}</td>
            <td>${item.listPrice}</td>
            <td>${item.supplierId}</td>
            <td>${item.attribute1}</td>
            <td><a href="./item?itemId=${item.itemId}">查看</a></td>
        </tr>
    </c:forEach>

</table>




</body>
</html>
