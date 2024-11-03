
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/top.jsp"%>
<html>
<head>
    <title>主页面</title>
</head>
<body>
    <h1><a href="./category?category=PYTHON">python</a> </h1>
    <h1><a href="./category?category=JAVA">java</a> </h1>
    <h1><a href="./category?category=C">c</a> </h1>
    <h1><a href="./category?category=C%2B%2B">c++</a> </h1>
    <form action="./main" method="post">
        <input type="text" name="information" placeholder="输入关键字搜索"/>
        <button type="submit">搜索</button>
    </form>
</body>
</html>
<%@include file="../common/bottom.jsp"%>

