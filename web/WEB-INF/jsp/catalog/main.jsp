<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/top.jsp"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>主页面</title>
    <link rel="stylesheet" href="css/main.css"> <!-- 引入外部CSS -->
</head>
<body>
<p>
    <br>
    <br>

</p>
<!-- 搜索表单 -->
<form action="./main" method="get" class="search-form">
    <input type="search" name="information" placeholder="输入关键字搜索">
</form>


<!-- 导航栏目菜单 -->
<div class="menu-container">
    <div class="menu-item" data-target="c++">C++</div>
    <div class="menu-item" data-target="java">Java</div>
    <div class="menu-item" data-target="c">C</div>
    <div class="menu-item" data-target="python">Python</div>
</div>

<!-- 悬浮选项卡 -->
<div class="hover-card" id="c++">C++的选项卡内容</div>
<div class="hover-card" id="java">Java的选项卡内容</div>
<div class="hover-card" id="c">C的选项卡内容</div>
<div class="hover-card" id="python">Python的选项卡内容</div>

<!-- 分类容器 -->
<div class="category-container">
    <!-- 第一行分类项 -->
    <a href="./category?category=PYTHON" class="category-box">
        <img src="images/python.png" alt="Python">
        <div class="category-text">Python</div>
    </a>
    <a href="./category?category=JAVA" class="category-box">
        <img src="mages/java.jpg" alt="Java">
        <div class="category-text">Java</div>
    </a>
    <a href="./category?category=C" class="category-box">
        <img src="images/c.jpg" alt="C">
        <div class="category-text">C</div>
    </a>
    <a href="./category?category=C%2B%2B" class="category-box">
        <img src="images/c++.jpg" alt="C++">
        <div class="category-text">C++</div>
    </a>

    <!-- 第二行分类项 -->
    <a href="./category?category=JAVASCRIPT" class="category-box">
        <img src="../../images/javascript.jpg" alt="JavaScript">
        <div class="category-text">JavaScript</div>
    </a>
    <a href="./category?category=PHP" class="category-box">
        <img src="../../images/php.jpg" alt="PHP">
        <div class="category-text">PHP</div>
    </a>
    <a href="./category?category=RUBY" class="category-box">
        <img src="../../images/ruby.jpg" alt="Ruby">
        <div class="category-text">Ruby</div>
    </a>
    <a href="./category?category=HTML" class="category-box">
        <img src="../../images/html.jpg" alt="HTML">
        <div class="category-text">HTML</div>
    </a>

    <!-- 第三行分类项 -->
    <a href="./category?category=CSS" class="category-box">
        <img src="../../images/css.jpg" alt="CSS">
        <div class="category-text">CSS</div>
    </a>
    <a href="./category?category=SQL" class="category-box">
        <img src="../../images/sql.jpg" alt="SQL">
        <div class="category-text">SQL</div>
    </a>
    <a href="./category?category=SWIFT" class="category-box">
        <img src="../../images/swift.jpg" alt="Swift">
        <div class="category-text">Swift</div>
    </a>
    <a href="./category?category=GO" class="category-box">
        <img src="../../images/go.jpg" alt="Go">
        <div class="category-text">Go</div>
    </a>

    <!-- 第四行分类项 -->
    <a href="./category?category=RUST" class="category-box">
        <img src="../../images/rust.jpg" alt="Rust">
        <div class="category-text">Rust</div>
    </a>
    <a href="./category?category=KOTLIN" class="category-box">
        <img src="../../images/kotlin.jpg" alt="Kotlin">
        <div class="category-text">Kotlin</div>
    </a>
    <a href="./category?category=SCALA" class="category-box">
        <img src="../../images/scala.jpg" alt="Scala">
        <div class="category-text">Scala</div>
    </a>
    <a href="./category?category=PERL" class="category-box">
        <img src="../../images/perl.jpg" alt="Perl">
        <div class="category-text">Perl</div>
    </a>

    <!-- 第五行分类项 -->
    <a href="./category?category=HASKELL" class="category-box">
        <img src="../../images/haskell.jpg" alt="Haskell">
        <div class="category-text">Haskell</div>
    </a>
    <a href="./category?category=R" class="category-box">
        <img src="../../images/r.jpg" alt="R">
        <div class="category-text">R</div>
    </a>
    <a href="./category?category=MATLAB" class="category-box">
        <img src="../../images/matlab.jpg" alt="MATLAB">
        <div class="category-text">MATLAB</div>
    </a>
    <a href="./category?category=VUEJS" class="category-box">
        <img src="../../images/vuejs.jpg" alt="Vue.js">
        <div class="category-text">Vue.js</div>
    </a>

    <!-- 第六行分类项 -->
    <a href="./category?category=REACTJS" class="category-box">
        <img src="../../images/reactjs.jpg" alt="React.js">
        <div class="category-text">React.js</div>
    </a>
    <a href="./category?category=ANGULAR" class="category-box">
        <img src="../../images/angular.jpg" alt="Angular">
        <div class="category-text">Angular</div>
    </a>
    <a href="./category?category=NODEJS" class="category-box">
        <img src="../../images/nodejs.jpg" alt="Node.js">
        <div class="category-text">Node.js</div>
    </a>
    <a href="./category?category=DJANGO" class="category-box">
        <img src="../../images/django.jpg" alt="Django">
        <div class="category-text">Django</div>
    </a>

    <!-- 第七行分类项 -->
    <a href="./category?category=FLASK" class="category-box">
        <img src="../../images/flask.jpg" alt="Flask">
        <div class="category-text">Flask</div>
    </a>
    <a href="./category?category=SPRING" class="category-box">
        <img src="../../images/spring.jpg" alt="Spring">
        <div class="category-text">Spring</div>
    </a>
    <a href="./category?category=LARAVEL" class="category-box">
        <img src="../../images/laravel.jpg" alt="Laravel">
        <div class="category-text">Laravel</div>
    </a>
    <a href="./category?category=EXPRESS" class="category-box">
        <img src="../../images/express.jpg" alt="Express">
        <div class="category-text">Express</div>
    </a>

    <!-- 第八行分类项 -->
    <a href="./category?category=RUBYONRAILS" class="category-box">
        <img src="../../images/rubyonrails.jpg" alt="Ruby on Rails">
        <div class="category-text">Ruby on Rails</div>
    </a>
    <a href="./category?category=WORDPRESS" class="category-box">
        <img src="../../images/wordpress.jpg" alt="WordPress">
        <div class="category-text">WordPress</div>
    </a>
    <a href="./category?category=SHOPIFY" class="category-box">
        <img src="../../images/shopify.jpg" alt="Shopify">
        <div class="category-text">Shopify</div>
    </a>
    <a href="./category?category=MAGENTO" class="category-box">
        <img src="../../images/magento.jpg" alt="Magento">
        <div class="category-text">Magento</div>
    </a>
</div>
<script>
    // 悬浮选项卡显示/隐藏逻辑
    const menuItems = document.querySelectorAll(".menu-item");
    const hoverCards = document.querySelectorAll(".hover-card");

    let isHovering = false; // 标志是否在悬浮窗或触发区域内

    menuItems.forEach(item => {
        item.addEventListener("mouseenter", () => {
            const target = item.getAttribute("data-target");
            const hoverCard = document.getElementById(target);

            isHovering = true; // 标志鼠标在触发区域
            hoverCard.style.display = "block"; // 显示悬浮窗
        });

        item.addEventListener("mouseleave", () => {
            const target = item.getAttribute("data-target");
            const hoverCard = document.getElementById(target);

            setTimeout(() => {
                if (!isHovering) {
                    hoverCard.style.display = "none"; // 如果鼠标已移出，隐藏悬浮窗
                }
            }, 50); // 延迟关闭，避免快速闪烁
        });
    });

    hoverCards.forEach(card => {
        card.addEventListener("mouseenter", () => {
            isHovering = true; // 标志鼠标在悬浮窗区域
            card.style.display = "block"; // 保持显示悬浮窗
        });

        card.addEventListener("mouseleave", () => {
            isHovering = false; // 鼠标移出悬浮窗区域
            setTimeout(() => {
                if (!isHovering) {
                    card.style.display = "none"; // 如果鼠标完全移出，隐藏悬浮窗
                }
            }, 50); // 延迟关闭，避免快速闪烁
        });
    });
</script>


</body>
</html>


