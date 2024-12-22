<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CMP</title>
    <link rel="stylesheet" href="css/top.css"> <!-- 引入外部CSS -->
</head>
<body>

<div class="top-container">
    <div class="top-bar">
        <div class="logo">
            <a href="main">CMP</a>
        </div>

        <div class="nav-links">
            <!-- 基于登录状态显示问候语或登录注册按钮 -->
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <span class="greeting">
                        <script>
                            // 获取当前时间并显示问候语
                            const currentHour = new Date().getHours();
                            const greeting =
                                currentHour < 12 ? "上午好" :
                                    currentHour < 18 ? "下午好" : "晚上好";

                            document.write(greeting + ", ${sessionScope.user.username}");
                        </script>
                    </span>
                    <a href="userInfo" class="btn">个人信息</a>
                    <a href="logout" class="btn">退出登录</a>
                </c:when>
                <c:otherwise>
                    <a href="loginForm" class="btn">登录/注册</a>
                </c:otherwise>
            </c:choose>

            <div class="button-group">
                <span class="hidden" id="username">${sessionScope.user.username}</span>  <%--存一下username--%>
                <button id="cartButton" class="btn">
                    购物车
                </button>
                <button id="myOrderButton" class="btn">
                    我的订单
                </button>
            </div>
        </div>
    </div>
</div>

<div id="cartContainer">
</div>
<div id="myOrderContainer">
</div>

</body>

<script src="js/topcart.js"></script>
<script src="js/topmyorder.js"></script>
</html>
