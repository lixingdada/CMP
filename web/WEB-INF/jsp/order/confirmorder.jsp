
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>填写订单信息</title>
    <link rel="stylesheet" href="css/2.css">
</head>


<body>
<div>
    <h2 class="centered-text">请输入具体信息</h2>
    <form action="orderForm?username=${sessionScope.username}" method="post" class="centered-text">
    <table class="centered-text">
        <tr class="centered-text">
            <td>
                姓名
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderName" >
            </td>
        </tr>

        <tr class="centered-text">
            <td>
                电话
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderTel" >
            </td>
        </tr>

        <tr class="centered-text">
            <td>
                地址
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderAddress" >
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
                合计: <fmt:formatNumber value="${sessionScope.cart.getSubTotal()}" pattern="$#,##0.00" />
            </td>
        </tr>

        <tr class="centered-text">
            <td colspan="2" class="centered-text">
                <a href="cartForm?username=${sessionScope.username}" class="Button centered-text">返回</a>
            </td>
            <td colspan="3" class="centered-text">
                <input type="submit" value="确定" class="Button centered-text">
            </td>
        </tr>
    </table>
    </form>
</div>
</body>
</html>

<%@ include file="../common/bottom.jsp"%>

