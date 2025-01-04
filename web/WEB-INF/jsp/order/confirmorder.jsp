
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../common/top.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>填写订单信息</title>
    <link rel="stylesheet" href="css/confirmorder.css">
</head>


<body>
<div>
    <h2>订单详情</h2>
    <span id="username" style="display:none">${sessionScope.username}</span>
    <form id="orderForm" action="orderForm?username=${sessionScope.username}" method="POST" >
        <h3>收货人信息</h3>
        <div>
        <table class="main-info">
            <tr>
                <td>
                    <label for="receiverSelect">请选择</label>
                </td>
                <td>
                    <select id="receiverSelect" name="selectedReceiver">
                        <c:forEach var="receiver" items="${sessionScope.receivers}">
                            <option value="${receiver.receiverName},${receiver.receiverPhone},${receiver.receiverAddress}">
                                    ${receiver.receiverName} - ${receiver.receiverPhone} - ${receiver.receiverAddress}
                            </option>
                        </c:forEach>
                    </select>
                </td>
            </tr>

        <tr>
            <td>
                <label>收货人</label>
            </td>
            <td>
                <c:if test="${sessionScope.receivers[0] != null}">
                    <input type="text" name="orderName" id="orderName" value="${sessionScope.receivers[0].receiverName}">
                </c:if>
                <c:if test="${sessionScope.receivers[0] == null}">
                    <input type="text" name="orderName" id="orderName" value="">
                </c:if>
            </td>
        </tr>

        <tr>
            <td>
                <label>电话</label>
            </td>
            <td>
                <c:if test="${sessionScope.receivers[0] != null}">
                    <input type="text" name="orderTel" id="orderTel" value="${sessionScope.receivers[0].receiverPhone}">
                </c:if>
                <c:if test="${sessionScope.receivers[0] == null}">
                    <input type="text" name="orderTel" id="orderTel" value="">
                </c:if>
            </td>
        </tr>

        <tr>
            <td>
                <label>地址</label>
            </td>
            <td>
                <c:if test="${sessionScope.receivers[0] != null}">
                    <input type="text" name="orderAddress" id="orderAddress" value="${sessionScope.receivers[0].receiverAddress}">
                </c:if>
                <c:if test="${sessionScope.receivers[0] == null}">
                    <input type="text" name="orderAddress" id="orderAddress" value="">
                </c:if>
            </td>
        </tr>
    </table>
        </div>

        <h3>购买详情</h3>
        <div class="cart-container">
            <table class="cart-item">
                <thead class="cart-header">
                <tr>
                    <th>图片</th>
                    <th>名称</th>
                    <th>数量</th>
                    <th>单价</th>
                    <th>总价</th>
                </tr>
                </thead>

                <tbody>
                    <c:forEach var="cartItem" items="${sessionScope.cart.getAllCartItems()}">
                        <c:set var="currentItem" value="${sessionScope.catalogService.getItem(cartItem.item.itemId)}"/>
                        <tr class="product-details">
                            <td class="product-image">
                                <img src="images/${currentItem.getAttribute2()}" alt="">
                            </td>
                            <td class="product-name">
                                <a href="item?itemId=${cartItem.item.itemId}&username=${sessionScope.username}" id="itemName">${currentItem.getAttribute3()}</a>
                            </td>
                            <td class="product-quantity">
                                <div class="quantity-wrapper">
                                    ${cartItem.quantity}
                                </div>
                            </td>
                            <td class="product-price" id="itemPrice">${cartItem.item.listPrice}</td>
                            <td class="product-total item-total" id="itemTotal">${cartItem.total}</td>
                        </tr>
                    </c:forEach>
                </tbody>

                <tfoot class="bottom-part">
                <tr class="bottom">
                    <td class="total-label">
                        合计：
                    </td>
                    <c:if test="${sessionScope.cart.getNumberOfItems() == 0 || sessionScope.cart == null}">
                        <td class="total-price">
                            0.00
                        </td>
                    </c:if>
                    <c:if test="${sessionScope.cart.getNumberOfItems() > 0}">
                        <td class="total-price" id="cartTotal">
                                ${sessionScope.cart.getSubTotal()}
                        </td>
                    </c:if>
                    <td class="text-container">
                        <a href="cartForm?username=${sessionScope.username}" type="button" class="return-button">返回</a>
                    </td>
                    <td class="text-container">
                        <button id="pay" type="button" class="pay">去支付</button>
                    </td>
                </tr>
                </tfoot>
            </table>
        </div>
    </form>
</div>
<script>
    document.getElementById('receiverSelect').addEventListener('change', function() {
        var selectedValue = this.value;
        var values = selectedValue.split(',');
        document.getElementById('orderName').value = values[0];
        document.getElementById('orderTel').value = values[1];
        document.getElementById('orderAddress').value = values[2];
    });

    document.getElementById('pay').onclick = function () {
        console.log('click');
        var username = document.getElementById('username').innerText;
        var orderName = document.getElementById('orderName').value;
        var orderAddress = document.getElementById('orderAddress').value;
        var orderTel = document.getElementById('orderTel').value;
        console.log(username+orderName+orderAddress+orderTel);
        if (orderName === '' || orderAddress === '' || orderTel === '') {
            console.log('empty');
            alert('请填写完整信息');
            event.preventDefault(); // 阻止表单提交
        } else {
            console.log('not empty');
            document.getElementById('orderForm').submit();
        }
    }
</script>
</body>
</html>



