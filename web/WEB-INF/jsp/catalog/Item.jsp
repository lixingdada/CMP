
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Item</title>
</head>
<body>
    <h1>商品名字: ${sessionScope.productName}</h1>
    <h1>价格:${sessionScope.item.listPrice}</h1>
    <h1>供应商: ${sessionScope.item.supplierId}</h1>
    <h1>状态: ${sessionScope.item.status}</h1>
    <h1>信息: ${sessionScope.item.attribute1}</h1>

    <form action="addItemToCart" method="post">
        <input type="hidden" name="itemId" value="${sessionScope.item.itemId}"/>
        <button type="submit">加入购物车</button>
    </form>

    <form action="" method="post">
        <input type="hidden" name="itemId" value="${sessionScope.item.itemId}"/>
        <button type="submit">购买</button>
    </form>
</body>
</html>