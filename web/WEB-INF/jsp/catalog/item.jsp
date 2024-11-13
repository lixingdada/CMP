
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/top.jsp"%>
<html>
<head>
    <title>Item</title>
    <link rel="stylesheet" type="text/css" href="css/item.css">
</head>
<body>

<div class="search-form">
    <form action="./main" method="post">
        <input type="text" name="information" placeholder="${requestScope.information}"/>
        <button type="submit">搜索</button>
    </form>
</div>
<br>
<div class="item-details-container">
    <div class="item-content">
        <div class="item-image">
            <!-- 点击图片时打开模态框 -->
            <img src="images/${sessionScope.item.attribute2}" alt="${sessionScope.productName}" onclick="openModal()">
        </div>
        <div class="item-info">
            <h1>商品名字: ${sessionScope.productName}</h1>
            <h2>价格: ¥${sessionScope.item.listPrice}</h2>
            <h2>供应商: ${sessionScope.item.supplierId}</h2>
            <h2>状态: ${sessionScope.item.status}</h2>
            <h2>信息: ${sessionScope.item.attribute1}</h2>

            <div class="button-container">
                <form action="addItemToCart" method="post" class="action-form">
                    <input type="hidden" name="itemId" value="${sessionScope.item.itemId}"/>
                    <input type="hidden" name="username" value="${sessionScope.user.username}"/>
                    <button type="submit" class="action-button">加入购物车</button>
                </form>

<%--                <form action="" method="post" class="action-form">--%>
<%--                    <input type="hidden" name="itemId" value="${sessionScope.item.itemId}"/>--%>
<%--                    <button type="submit" class="action-button">购买</button>--%>
<%--                </form>--%>
            </div>

            <h2><a href="product" class="return-link">返回</a></h2>
        </div>
    </div>
</div>

<!-- 模态框结构 -->
<div id="imageModal" class="modal">
    <span class="close" onclick="closeModal()">&times;</span>
    <img class="modal-content" id="modalImage">
</div>

<script>
    // 打开模态框并设置大图
    function openModal() {
        const modal = document.getElementById("imageModal");
        const modalImage = document.getElementById("modalImage");
        modal.style.display = "block";
        modalImage.src = "images/${sessionScope.item.attribute2}";
    }

    // 关闭模态框
    function closeModal() {
        document.getElementById("imageModal").style.display = "none";
    }
</script>


</body>
</html>