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
    <h2 class="centered-text">确认订单</h2>
    <form action="" method="post" class="centered-text">
    <table class="centered-text">
        <tr class="centered-text">
            <td>
                姓名：
            </td>
            <td class="centered-text">
                <input type="text" name="orderName" value="${sessionScope.order.orderName}">
            </td>
        </tr>

        <tr class="centered-text">
            <td>
                电话
            </td>
            <td class="centered-text">
                <input type="text" name="orderTel" value="${sessionScope.order.orderTel}">
            </td>
        </tr>

        <tr class="centered-text">
            <td>
                地址
            </td>
            <td class="centered-text">
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
                <a href="mainForm" class="Button centered-text">去支付</a>
            </td>
        </tr>
    </table>
    </form>
</div>

<%@ include file="../common/bottom.jsp"%>

