

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>top</title>
    <link rel="stylesheet" href="css/top.css"> <!-- 引入外部CSS -->
</head>
<body>

<div class="top-container">
    <div class="top-bar">
        <div class="logo">
            <a href="main">CMP</a>
        </div>

        <div class="nav-links">
            <!-- 判断用户是否登录 -->
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <span style="color: #007BFF;">欢迎, ${sessionScope.user.username}</span>
                    <a href="" class="btn">退出登录</a>
                </c:when>
                <c:otherwise>
                    <a href="" class="btn">登录</a>
                    <a href="" class="btn">注册</a>
                </c:otherwise>
            </c:choose>

            <a href="" class="cart-link">
                <span class="cart-icon">&#128722;</span>购物车
            </a>
        </div>
    </div>
</div>


</body>
</html>



<%--<!DOCTYPE html>

<html>

<head>
    <title>CMP</title>
    <link rel="StyleSheet" href="css/1.css" type="text/css"
          media="screen" />
</head>

<body>

<div id="Header">

    <div id="Logo">
        <div id="LogoContent">
            <a href="mainForm"><img src="images/logo-topbar.gif" alt="返回"></a>
        </div>
    </div>--%>

    <div id="Menu">
        <div id="MenuContent">
            <a href="cartForm"><img align="middle" name="img_cart" src="images/cart.gif" /></a>
            <img align="middle" src="images/separator.gif" />
            <a href="loginForm">Sign In</a>
            <a href="#">Sign Out</a>
            <img align="middle" src="images/separator.gif" />
            <a href="#"> My Account</a>
            <img align="middle" src="images/separator.gif" />
            <a href="myOrderForm">?????</a></div>
    </div>

    <div id="Search">
        <div id="SearchContent">
            <form action="" method="post">
                <input type="text" name="keyword" size="14">
                <input type="submit" value="search">
            </form>
        </div>
    </div>

    <div id="QuickLinks">
        <a href=""><img src="images/sm_fish.gif" /></a>
        <img src="images/separator.gif" />
        <a href=""> <img src="images/sm_dogs.gif" /></a>
        <img src="images/separator.gif" />
        <a href=""><img src="images/sm_reptiles.gif" /></a>
        <img src="images/separator.gif" />
        <a href=""><img src="images/sm_cats.gif" /></a>
        <img src="images/separator.gif" />
        <a href=""> <img src="images/sm_birds.gif" /></a>
        <img src="images/separator.gif" />
    </div>

</div>

<div id="Content" ></div>