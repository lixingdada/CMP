
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="../common/top.jsp"%>

<style>
    .centered-text {
        text-align: center;
    }
</style>
<div id="BackLink">
    <a href="mainForm"> 返回主菜单</a>
</div>

<div id="Catalog">
    <div id="Cart">
        <h2 class="centered-text">购物车</h2>

        <form action="updateCart" method="post" class="centered-text">
            <table class="centered-text">
                <tr>
                    <th class="centered-text"><b>名称</b></th>
                    <th class="centered-text"><b>数量</b></th>
                    <th class="centered-text"><b>单价</b></th>
                    <th class="centered-text"><b>总价</b></th>
                    <th class="centered-text">&nbsp;</th>
                </tr>

                <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart ==null}">
                    <tr>
                        <td colspan="5" class="centered-text">您的购物车空空如也</td>
                    </tr>
                </c:if>

                <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                <c:forEach var="cartItem" items="${sessionScope.cart.cartItems}">
                    <tr>
                        <td class="centered-text">
                            <a href="itemForm?itemId=${cartItem.item.itemId}">${cartItem.item.itemId}</a>
                        </td>

                        <td class="centered-text">
                            <input type="text" name="${cartItem.item.itemId}" value="${cartItem.quantity}">
                        </td>

                        <td class="centered-text"><fmt:formatNumber value="${cartItem.item.listPrice}"
                                                                    pattern="$#,##0.00" /></td>

                        <td class="centered-text"><fmt:formatNumber value="${cartItem.total}"
                                                                    pattern="$#,##0.00" /></td>

                        <td class="centered-text">
                            <a href="removeCartItem?workingItemId=${cartItem.item.itemId}" class="Button">移除</a>
                        </td>
                    </tr>
                </c:forEach>
                </c:if>

                <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart ==null}">
                    <tr>
                        <td colspan="5" class="centered-text">
                            合计: $0.00
                        </td>
                    </tr>
                </c:if>

                <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                <tr>
                    <td colspan="5" class="centered-text">
                        合计: <fmt:formatNumber value="${sessionScope.cart.getsubTotal()}" pattern="$#,##0.00" />
                    </td>
                </tr>
                </c:if>

                <tr>
                    <td colspan="2" class="centered-text">
                        <input type="submit" value="更新">
                    </td>

                    <td colspan="3" class="centered-text">
                        <a href="confirmOrderForm" class="Button centered-text">提交订单</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<%@ include file="../common/bottom.jsp"%>