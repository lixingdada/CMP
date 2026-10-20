
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
        <br>

        <!-- 左右布局：左侧为图片，右侧为信息 -->
        <div class="page-container">
            <!-- 左侧图片部分 -->
            <div class="left-image">
                <img id="product-image" src="images/${sessionScope.item.attribute2}" alt="商品图片" />
            </div>

            <!-- 右侧信息部分 -->
            <div class="right-info">
                <!-- 文字信息 -->
                <div class="info-content">
                    <p id="itemName">商品名字: ${sessionScope.item.attribute3}</p>
                    <p >供应商: ${sessionScope.item.supplierId}</p>
                    <p>状态: ${sessionScope.item.status}</p>
                    <p>类型: ${sessionScope.item.attribute1}</p>
                    <p id="price"> ¥${sessionScope.item.listPrice}</p>

                    <!-- 菜单选项部分 -->
                    <div class="menu-options">
                        <a href="addItemToCart?itemId=${sessionScope.item.itemId}&username=${sessionScope.user.username}" class="menu-option">加入购物车</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- 弹窗查看图片 -->
        <div class="image-modal">
            <img class="modal-content" src="" alt="查看大图">
            <span class="close-btn">&times;</span>
        </div>

        <script src="js/item.js"></script>
        </body>
        </html>
