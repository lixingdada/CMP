<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
</head>

<body>
    这是<a href="./item">${sessionScope.category}</a>
    <table>
        <tr>
            <td>产品id</td>
            <td>产品名字</td>
        </tr>
        <c:forEach items="${sessionScope.productList}" var="product">
            <tr>
                <td>${product.productId}</td>
                <td>${product.name}</td>
                <td>
                    <form action="./item" method="post" accept-charset="UTF-8">
                        <input type="hidden" name="productId" value="${product.productId}" />
                        <input type="hidden" name="productName" value="${product.name}" />
                        <button type="submit">选择</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>

</body>
</html>
