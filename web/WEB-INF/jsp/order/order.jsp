
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>订单</title>
    <link rel="stylesheet" href="css/2.css">
</head>


<body>
<div>
    <h2 class="centered-text">订单详情</h2>
    <form action="submitOrderForm?username=${sessionScope.username}" method="post" class="centered-text">
<%--隐藏字段--%>
        <input type="hidden" name="orderName" value="${sessionScope.order.orderName}"/>
        <input type="hidden" name="orderTel" value="${sessionScope.order.orderTel}"/>
        <input type="hidden" name="orderAddress" value="${sessionScope.order.orderAddress}"/>


        <table class="centered-text">
            <tr class="centered-text">
                <td>
                    姓名
                </td>
                <td class="centered-text">
                    ${sessionScope.order.orderName}"
                </td>
            </tr>

            <tr class="centered-text">
                <td>
                    电话
                </td>
                <td class="centered-text">
                    ${sessionScope.order.orderTel}"
                </td>
            </tr>

            <tr class="centered-text">
                <td>
                    地址
                </td>
                <td class="centered-text">
                    ${sessionScope.order.orderAddress}"
                </td>
            </tr>

                    <%--  罗列具体商品信息   --%>


            <tr>
                <td  class="centered-text">
                    <a href="confirmOrderForm?username=${sessionScope.username}" class="Button centered-text">返回</a>
                </td>
                <td  class="centered-text">
                    <input type="submit" value="去支付" class="Button centered-text">
                </td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>
<%@ include file="../common/bottom.jsp"%>

