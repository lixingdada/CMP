<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="ActionButtons">
    <!-- 显示总价格 -->
    <div class="total-price">
        合计: <fmt:formatNumber value="${sessionScope.cart.getSubTotal()}" pattern="$#,##0.00" />
    </div>
    <!-- 操作按钮 -->
    <form action="updateCart?username=exampleUser" method="post" class="centered-text">
        <div class="text-container">
            <span class="text-button" onclick="submitForm()">更新</span>
            <span class="text-button" onclick="navigateTo('mainForm?username=exampleUser')">继续购物</span>
            <span class="text-button" onclick="navigateTo('confirmOrderForm?username=exampleUser')">提交订单</span>
        </div>
    </form>
</div>

<script>
    // 提交表单
    function submitForm() {
        document.querySelector('form').submit();
    }

    // 页面跳转
    function navigateTo(url) {
        window.location.href = url;
    }
</script>
