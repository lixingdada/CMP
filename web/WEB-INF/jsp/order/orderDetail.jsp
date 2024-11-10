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


<h1>订单详情</h1>


<table class="centered-text">
    <tr class="centered-text">
        <th class="centered-text">商品ID</th>
        <th class="centered-text">数量</th>
        <th class="centered-text">价格</th>
    </tr>
    <c:forEach var="item" items="${orderItems}">
        <tr class="centered-text">
            <td class="centered-text">${item.itemId}</td>
            <td class="centered-text">${item.quantity}</td>
            <td class="centered-text">${item.price}</td>
        </tr>
    </c:forEach>
</table>
<%@ include file="../common/bottom.jsp" %>

