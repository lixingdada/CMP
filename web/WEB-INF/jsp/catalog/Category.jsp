<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
</head>

<body>
    <%--<h1>Session msg 值: ${sessionScope.msg}</h1>--%>

<%--如果是搜索的话--%>
<c:if test="${sessionScope.msg=='search'}">
        <form action="./main" method="post">
            <input type="text" name="information" placeholder="输入关键字搜索"/>
            <button type="submit">搜索</button>
        </form>
        <%--<h1>hhh1</h1>--%>
    </c:if>

<%--如果是点击大板块进来的话--%>
    <c:if test="${sessionScope.msg.equals('')||sessionScope.msg==null}">
        <h1>${sessionScope.category}</h1>
       <%-- <h1>hhh2</h1>--%>
    </c:if>

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
                    <form action="./product" method="post" accept-charset="UTF-8">
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
