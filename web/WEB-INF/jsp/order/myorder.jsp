<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f7f7f7;
        color: #333;
    }

    #BackLink {
        margin: 20px;
        text-align: right;
    }

    #Catalog {
        max-width: 800px;
        margin: 0 auto;
        padding: 20px;
        background-color: #ffffff;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    #Cart h2 {
        color: #333;
        font-size: 24px;
        margin-bottom: 20px;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
    }

    th, td {
        padding: 12px;
        text-align: center;
        border: 1px solid #ddd;
    }

    th {
        background-color: #4CAF50;
        color: white;
    }

    tr:nth-child(even) {
        background-color: #f2f2f2;
    }

    input[type="text"] {
        width: 50px;
        text-align: center;
    }

    .Button {
        background-color: #007BFF;
        color: white;
        padding: 5px 5px; /* 增大内边距 */
        font-size: 20px; /* 增大字体大小 */
        text-decoration: none;
        border-radius: 5px;
        transition: background-color 0.3s;
    }

    .Button:hover {
        background-color: #0056b3;
    }

    /* 如果有提交按钮，可以增加相同的样式 */
    input[type="submit"] {
        background-color: #007BFF;
        color: white;
        padding: 5px 5px; /* 增大内边距 */
        font-size: 20px; /* 增大字体大小 */
        border: none;
        border-radius: 5px;
        cursor: pointer;
        transition: background-color 0.3s;
    }

    input[type="submit"]:hover {
        background-color: #0056b3;
    }

    .centered-text {
        text-align: center;
    }
</style>

<div>
    <h2 class="centered-text">我的订单</h2>
    <form action="myOrderForm" method="post" class="centered-text">
        <table class="centered-text">
            <tr class="centered-text">
                <th class="centered-text">订单ID</th>
                <th class="centered-text">订单时间</th>
                <th class="centered-text">收件人</th>
                <th class="centered-text">收货地址</th>
                <th class="centered-text">联系电话</th>
            </tr>

            <c:forEach var="order" items="${myOrder}">
                <tr class="centered-text">
                    <td>
                        <a href="orderDetailForm?orderId=${order.orderId}" class="centered-text">${order.orderId}</a>
                    </td>

                    <td class="centered-text">
                        <fmt:formatDate value="${order.orderDate}" pattern="yyyy/MM/dd hh:mm:ss" />
                    </td>

                    <td class="centered-text">${order.orderName}</td>

                    <td class="centered-text">${order.orderAddress}</td>

                    <td class="centered-text">${order.orderTel}</td>
                </tr>
            </c:forEach>
        </table>
    </form>
    <div class="centered-text">
        <a href="mainForm" class="Button centered-text">返回首页</a>
    </div>
</div>
<%@ include file="../common/bottom.jsp" %>
