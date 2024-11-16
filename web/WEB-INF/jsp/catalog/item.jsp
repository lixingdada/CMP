
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
        </div>
    </div>

    <!-- 加入购物车按钮 -->
    <div class="button-container">
        <form action="addItemToCart" method="post" class="action-form">
            <input type="hidden" name="itemId" value="${sessionScope.item.itemId}"/>
            <input type="hidden" name="username" value="${sessionScope.user.username}"/>
            <button type="submit" class="action-button">加入购物车</button>
        </form>
    </div>
</div>

<!-- 返回按钮 -->
<div class="back-link-container">
    <form action="product" method="post">
        <button type="submit" class="back-link">返回</button>
    </form>
</div>

<!-- 模态框 -->
<div id="imageModal" class="modal">
    <span class="close" onclick="closeModal()">&times;</span>
    <img class="modal-content" id="modalImage">
</div>

<script>
    // 打开模态框
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