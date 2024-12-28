
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/top.jsp"%>
<html>
<head>
    <title>Item</title>
    <link rel="stylesheet" type="text/css" href="css/item.css">
</head>
<body>

<form action="./main" method="post" class="search-form">
    <input type="text" name="information" placeholder="${requestScope.information}"/>
    <button type="submit" class="image-button">
        <img src="${pageContext.request.contextPath}/images/searchicon.png" alt="搜索" />
    </button>
</form>

<br>

<div class="item-container">
    <!-- 左侧：商品图片 -->
    <div class="item-image">
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@include file="../common/top.jsp"%>
        <html>
        <head>
            <title>Item</title>
            <link rel="stylesheet" type="text/css" href="css/item.css">
        </head>
        <body>



        <!-- 左右布局：左侧为图片，右侧为信息 -->
        <div class="page-container">
            <!-- 左半部分：商品图片 -->
            <div class="left-image">
                <img src="images/${sessionScope.item.attribute2}" alt="${sessionScope.productName}">
            </div>

            <!-- 右半部分：商品信息 -->
            <div class="right-info">
                <!-- 圆角矩形块 -->
                <div class="info-overlay"></div>

                <!-- 文字信息 -->
                <div class="info-content">
                    <h1>商品名字: ${sessionScope.productName}</h1>
                    <h2>价格: ¥${sessionScope.item.listPrice}</h2>
                    <h2>供应商: ${sessionScope.item.supplierId}</h2>
                    <h2>状态: ${sessionScope.item.status}</h2>
                    <h2>信息: ${sessionScope.item.attribute1}</h2>

                    <!-- 菜单选项部分 -->
                    <div class="menu-options">
                        <a href="addItemToCart?itemId=${sessionScope.item.itemId}&username=${sessionScope.user.username}" class="menu-option">加入购物车</a>
                        <a href="product" class="menu-option">返回</a>

                    </div>
                </div>
            </div>
        </div>
        </body>
        </html>
