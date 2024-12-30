<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CMP</title>
    <link rel="stylesheet" href="css/top.css"> <!-- 引入外部CSS -->
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>

<div class="top-container">
    <div class="top-bar">
        <div class="logo">
            <a href="main">CMP</a>
        </div>

        <div class="nav-links">
            <!-- 基于登录状态显示问候语或登录注册按钮 -->
<%--            <c:choose>--%>
<%--                <c:when test="${sessionScope.user != null}">--%>
                    <span class="greeting">
                        <c:if test="${not empty sessionScope.user.username}">
                            ,${sessionScope.user.username}
                        </c:if>
                    </span>
<%--                    <a href="userInfo" class="btn">个人信息</a>--%>
<%--                    <a href="logout" class="btn">退出登录</a>--%>
<%--                </c:when>--%>
<%--                <c:otherwise>--%>
<%--                    <a href="loginForm" class="btn">登录/注册</a>--%>
<%--                </c:otherwise>--%>
<%--            </c:choose>--%>

<%--            <div class="button-group">--%>
<%--                <a href="javascript:void(0)" class="btn" onclick="handleRedirect('cartForm?username=${sessionScope.user.username}')">--%>
<%--                    &#128722; 购物车--%>
<%--                </a>--%>
<%--                <a href="javascript:void(0)" class="btn" onclick="handleRedirect('myOrderForm?username=${sessionScope.user.username}')">--%>
<%--                    我的订单--%>
<%--                </a>--%>
<%--            </div>--%>

<%--            头像框--%>
            <div class="profile-container">
                <div class="profile-circle">
                    <img src="" alt="头像" class="profile-image">
                </div>
                <div class="hover-card1">
                    <!-- 这里可以放置悬浮卡片的内容 -->
                    <c:if test="${not empty sessionScope.user.username}">
                        <p><a href="userInfo" class="btn">个人信息</a></p>
                        <p><a href="javascript:void(0)" class="btn" onclick="handleRedirect('cartForm?username=${sessionScope.user.username}')">
                            &#128722; 购物车
                        </a></p>
                        <p> <a href="javascript:void(0)" class="btn" onclick="handleRedirect('myOrderForm?username=${sessionScope.user.username}')">
                                我的订单
                            </a></p>
                        <a href="logout" class="btn">退出登录</a>
                    </c:if>
                    <c:if test="${empty sessionScope.user.username}">
                        <p><a href="loginForm" class="btn">登录/注册</a></p>

                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // 检查用户是否已登录
    const isUserLogIn = ${not empty sessionScope.user.username ? true : false};

    function handleRedirect(url) {
        if (!isUserLogIn) {
            alert("您尚未登录，点击确认后1秒后自动跳转！");
            setTimeout(() => window.location.href = "loginForm", 1000);
        } else {
            window.location.href = url;
        }
    }
</script>

<script src="js/top.js"></script>

</body>
</html>
