<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/top.jsp"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>主页面</title>
    <link rel="stylesheet" href="css/main.css"> <!-- 引入外部CSS -->
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>
<p>
    <br>
    <br>
    <br>
    <br>
</p>
<!-- 搜索表单 -->
<div id="search">
    <form action="./product" method="post" class="search-form">
        <div class="search-container">
            <input type="search" name="information" id="information" placeholder="输入关键字搜索">
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

<!-- 导航栏目菜单 -->
<div class="menu-container">
    <div class="menu-item" data-target="C%2B%2B">C++</div>
    <div class="menu-item" data-target="JAVA">Java</div>
    <div class="menu-item" data-target="C">C</div>
    <div class="menu-item" data-target="PYTHON">Python</div>
</div>

<!-- 悬浮选项卡 -->
<div class="hover-card">
</div>
<%--<div class="hover-card" id="c++">C++的选项卡内容</div>--%>
<%--<div class="hover-card" id="java">Java的选项卡内容</div>
<div class="hover-card" id="c">C的选项卡内容</div>
<div class="hover-card" id="python">Python的选项卡内容</div>--%>

<!-- 分类容器 -->
<div class="category-container">
    <c:forEach items="${sessionScope.allItems}" var="item">
        <a href="item?itemId=${item.itemId}&username=${sessionScope.user.username}" class="category-box">
            <img class="activeImg" src="images/${item.attribute2}" alt="${item.attribute3}">
            <div class="category-text">${item.attribute3}</div>
            <div class="category-price">￥${item.listPrice}</div>

            <!-- 覆盖卡片 -->
            <div class="overlay-card">
                <p class="overlay1">基本信息</p>
                <p class="overlay2">商品名称：${item.attribute3}</p>
                <p class="overlay2">商品类型：${item.attribute1}</p>
                <p class="overlay3">供应商：${item.supplierId}</p>
                <p class="overlay4">状态：${item.status}</p>
                <p class="overlay5">价格：￥${item.listPrice}</p>
            </div>
        </a>
    </c:forEach>
</div>

<script src="./js/main.js"></script>
<script src="./js/showItemInfo.js"></script>

</body>
</html>


