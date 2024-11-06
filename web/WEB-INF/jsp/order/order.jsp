<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common/top.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
    .centered-text {
        text-align: center;
    }
</style>

<div>
    <h2 class="centered-text">订单${sessionScope.order.orderId}</h2>
    <form class="centered-text">
        <table class="centered-text">
            <tr class="centered-text">
                <td>
                    姓名：
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

            <tr>
                <td class="centered-text">
                    <a href="submitOrderForm" class="Button centered-text">确定</a>
                </td>
            </tr>
        </table>
    </form>
</div>

<%@ include file="../common/bottom.jsp"%>

