
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
                    <b>姓名</b>
                </td>
                <td class="centered-text" colspan="4">
                    ${sessionScope.order.orderName}"
                </td>
            </tr>

            <tr class="centered-text">
                <td>
                    <b>电话</b>
                </td>
                <td class="centered-text" colspan="4">
                    ${sessionScope.order.orderTel}"
                </td>
            </tr>

            <tr class="centered-text">
                <td>
                    <b>地址</b>
                </td>
                <td class="centered-text" colspan="4">
                    ${sessionScope.order.orderAddress}"
                </td>
            </tr>


            <tr>
                <th class="centered-text"><b>名称</b></th>
                <th class="centered-text"><b>数量</b></th>
                <th class="centered-text"><b>单价</b></th>
                <th class="centered-text"><b>总价</b></th>
            </tr>
                    <%--  罗列具体商品信息   --%>
           <c:forEach var="cartItem" items="${sessionScope.order.getCart().getCartItemList()}">
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
                    合计: <fmt:formatNumber value="${sessionScope.order.getCart().getSubTotal()}" pattern="$#,##0.00" />
                </td>
            </tr>

            <tr>
                <td  class="centered-text" colspan="2">
                    <a href="confirmOrderForm?username=${sessionScope.username}" class="Button centered-text">返回</a>
                </td>
                <td  class="centered-text" colspan="3">
                    <input type="submit" value="去支付" class="Button centered-text">
                </td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>


