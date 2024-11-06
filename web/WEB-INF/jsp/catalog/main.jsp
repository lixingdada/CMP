
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/top.jsp"%>
<html>
<head>
    <title>主页面</title>
    <link rel="stylesheet" href="css/main.css"> <!-- 引入外部CSS -->
</head>
<body>

<div class="search-form">
    <form action="./main" method="post">
        <input type="text" name="information" placeholder="输入关键字搜索"/>
        <button type="submit">搜索</button>
    </form>
</div>

<div class="category-container">
    <a href="./category?category=PYTHON" class="category-box">
        <img src="images/python.png" alt="Python">
        <div class="category-text">Python</div>
    </a>
    <a href="./category?category=JAVA" class="category-box">
        <img src="images/java.jpg" alt="Java">
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
</div>




</body>
</html>
<%@include file="../common/bottom.jsp"%>

