
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@include file="../common/top.jsp"%>
        <html>
        <head>
            <title>Item</title>
            <link rel="stylesheet" type="text/css" href="css/item.css">
        </head>
        <body>

        <p>
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
                    <h1>商品名字: ${sessionScope.item.attribute3}</h1>
                    <h2>价格: ¥${sessionScope.item.listPrice}</h2>
                    <h2>供应商: ${sessionScope.item.supplierId}</h2>
                    <h2>状态: ${sessionScope.item.status}</h2>
                    <h2>信息: ${sessionScope.item.attribute1}</h2>

                    <!-- 菜单选项部分 -->
                    <div class="menu-options">
                        <a href="addItemToCart?itemId=${sessionScope.item.itemId}&username=${sessionScope.user.username}" class="menu-option">加入购物车</a>
<%--                        <a href="product" class="menu-option">返回</a>--%>

                    </div>
                </div>
            </div>
        </div>
        <script src="js/item.js"></script>
        </body>
        </html>
