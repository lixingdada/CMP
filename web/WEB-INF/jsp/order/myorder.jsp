<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .centered-text {
        text-align: center;
    }

    /* Modal styles */
    .modal {
        display: none; /* Hidden by default */
        position: fixed; /* Stay in place */
        z-index: 1; /* Sit on top */
        padding-top: 100px; /* Location of the box */
        left: 0;
        top: 0;
        width: 100%; /* Full width */
        height: 100%; /* Full height */
        overflow: auto; /* Enable scroll if needed */
        background-color: rgb(0,0,0); /* Fallback color */
        background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
    }

    .modal-content {
        background-color: #fefefe;
        margin: auto;
        padding: 20px;
        border: 1px solid #888;
        width: 80%;
    }

    .close {
        color: #aaaaaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
    }

    .close:hover,
    .close:focus {
        color: #000;
        text-decoration: none;
        cursor: pointer;
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
