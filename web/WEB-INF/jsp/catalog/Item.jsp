<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Item</title>
</head>
<body>
    这是${requestScope.productName}的item
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
                <form action="" method="post">
                    <input type="hidden" name="productId" value="${item.itemId}"/>
                    <button type="submit">加入购物车</button>
                </form>

                <form action="" method="post">
                    <input type="hidden" name="productId" value="${item.itemId}"/>
                    <button type="submit">购买</button>
                </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
