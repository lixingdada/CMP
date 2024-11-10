<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common/top.jsp"%>
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
    <h2 class="centered-text">确认订单</h2>
    <form action="" method="post" class="centered-text">
    <table class="centered-text">
        <tr class="centered-text">
            <td>
                姓名：
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderName" value="${sessionScope.order.orderName}">
            </td>
        </tr>

        <tr class="centered-text">
            <td>
                电话
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderTel" value="${sessionScope.order.orderTel}">
            </td>
        </tr>

        <tr class="centered-text">
            <td>
                地址
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderAddress" value="${sessionScope.order.orderAddress}">
            </td>
        </tr>

        <tr>
            <th class="centered-text"><b>名称</b></th>
            <th class="centered-text"><b>数量</b></th>
            <th class="centered-text"><b>单价</b></th>
            <th class="centered-text"><b>总价</b></th>
        </tr>

        <c:forEach var="cartItem" items="${sessionScope.cart.cartItems}">
            <tr>
                <td class="centered-text">
                    ${cartItem.item.itemId}
                </td>

                <td class="centered-text">
                   ${cartItem.quantity}"
                </td>

                <td class="centered-text"><fmt:formatNumber value="${cartItem.item.listPrice}"
                                                            pattern="$#,##0.00" /></td>

                <td class="centered-text"><fmt:formatNumber value="${cartItem.total}"
                                                            pattern="$#,##0.00" /></td>

            </tr>
        </c:forEach>

        <tr>
            <td colspan="5" class="centered-text">
                合计: <fmt:formatNumber value="${sessionScope.cart.getsubTotal()}" pattern="$#,##0.00" />
            </td>
        </tr>

        <tr class="centered-text">
            <td colspan="2" class="centered-text">
                <a href="cartForm" class="Button centered-text">返回</a>
            </td>
            <td colspan="3" class="centered-text">
                <a href="orderForm" class="Button centered-text">去支付</a>
            </td>
        </tr>
    </table>
    </form>
</div>

<%@ include file="../common/bottom.jsp"%>

