
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
                <td class="centered-text">
                    <label for="receiverSelect" class="centered-text"><b>请选择</b></label>
                </td>
                <td class="centered-text" colspan="4">
                    <select id="receiverSelect" name="selectedReceiver">
                        <c:forEach var="receiver" items="${sessionScope.receivers}">
                            <option value="${receiver.receiverName},${receiver.receiverPhone},${receiver.receiverAddress}">
                                    ${receiver.receiverName} - ${receiver.receiverPhone} - ${receiver.receiverAddress}
                            </option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </table>


        <table class="centered-text">
        <tr class="centered-text">
            <td class="centered-text">
                <b>收货人</b>
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderName" id="orderName" value="${not empty sessionScope.receivers[0] ? sessionScope.receivers[0].receiverName : ''}">
            </td>
        </tr>

        <tr class="centered-text">
            <td class="centered-text">
                <b>电话</b>
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderTel" id="orderTel" value="${not empty sessionScope.receivers[0] ? sessionScope.receivers[0].receiverPhone : ''}">
            </td>
        </tr>

        <tr class="centered-text">
            <td class="centered-text">
                <b>地址</b>
            </td>
            <td class="centered-text" colspan="4">
                <input type="text" name="orderAddress" id="orderAddress" value="${not empty sessionScope.receivers[0] ? sessionScope.receivers[0].receiverAddress : ''}">
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

<script>
    document.getElementById('receiverSelect').addEventListener('change', function() {
        const selectedValue = this.value.split(',');
        document.getElementById('orderName').value = selectedValue[0];
        document.getElementById('orderTel').value = selectedValue[1];
        document.getElementById('orderAddress').value = selectedValue[2];
    });
</script>

</body>
</html>

<%@ include file="../common/bottom.jsp"%>

