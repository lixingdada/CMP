<%@ page import="mvc.service.CatalogService" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/top.jsp"%>

<jsp:useBean id="catalogService" class="mvc.service.CatalogService" scope="page" />

<html>
<head>
    <title>category</title>
    <link rel="stylesheet" href="css/category.css?ts=<%= System.currentTimeMillis() %>">

</head>

<body>
<%--<h1>Session msg 值: ${sessionScope.msg}</h1>--%>

<%--如果是搜索的话--%>
<%--<c:if test="${sessionScope.msg=='search'}">--%>
<form action="./main" method="post" class="search-form">
    <input type="text" name="information" placeholder="${requestScope.information}"/>

    <button type="submit" class="image-button">
        <img src="${pageContext.request.contextPath}/images/searchicon.png" alt="搜索" />
    </button>
</form>
<%--<h1>hhh1</h1>--%>
<%--</c:if>--%>


<!-- 中心化显示内容 -->
<div class="breadcrumb-container">
    <c:if test="${sessionScope.msg=='flag'}">
        <h2 class="breadcrumb-title">以下是有关${sessionScope.category}的内容</h2>
    </c:if>

    <c:if test="${empty sessionScope.productList}">
        <h2 class="breadcrumb-title">没找到相关内容</h2>
    </c:if>
</div>



<c:if test="${not empty sessionScope.productList}">
    <%--<table>
        <tr>
            <td>产品id</td>
            <td>产品名字</td>
        </tr>--%>
<div class="product-container">
    <c:forEach items="${sessionScope.productList}" var="product">
        <%-- <tr>
             <td>${product.productId}</td>
             <td>${product.name}</td>
             <td>
                 <form action="./product" method="post" accept-charset="UTF-8">
                     <input type="hidden" name="productId" value="${product.productId}" />
                     <input type="hidden" name="productName" value="${product.name}" />
                     <button type="submit">选择</button>
                 </form>
             </td>
         </tr>--%>
        <c:set var="url" value="${catalogService.getUrlByProductId(product.productId)}" />
        <form action="./product" method="post" class="product-box" onclick="this.submit()">
            <input type="hidden" name="productId" value="${product.productId}" />
            <input type="hidden" name="productName" value="${product.name}" />

            <img src="images/${url}" alt="${product.name}">
            <div class="product-text">${product.name}</div>

        </form>
    </c:forEach>
        <%--</table>--%>
    </c:if>


</div>

<div class="back-link-container">
    <a href="main" class="back-link">返回</a>
</div>
</body>
</html>
