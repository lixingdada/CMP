
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common/top.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>订单OK</title>
    <link rel="stylesheet" href="css/okorder.css">
</head>

<body>
<div id="Catalog">
    <div id="Cart" class="centered-text">
        <p class="centered-text">订单提交成功</p>
        <a href="cartForm?username=${sessionScope.username}" class="Button centered-text">确定</a>
    </div>
</div>
</body>
</html>


