<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/top.jsp" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>个人信息</title>
    <link rel="stylesheet" href="css/userInfo.css">
</head>
<body>

<!-- 个人信息卡片 -->
<div class="profile-card">
    <div class="profile-header">
        <h2>个人信息</h2>
    </div>
    <div class="profile-content">
        <p><strong>头像:</strong> <img src="images/${sessionScope.user.avatar}" alt="头像" class="avatar"></p>
        <p><strong>账号:</strong> ${sessionScope.user.username}</p>
        <p><strong>昵称:</strong> ${sessionScope.user.virtualName}</p>
        <p><strong>生日:</strong> ${sessionScope.user.birthday}</p>
        <p><strong>邮箱:</strong> ${sessionScope.user.email}</p>
        <p><strong>电话:</strong> ${sessionScope.user.phone}</p>
        <button class="btn" onclick="showEditModal()">编辑</button>
    </div>
</div>

<!-- 编辑个人信息的悬浮窗口 -->
<div id="editProfileModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal('editProfileModal')">&times;</span>
        <h2>编辑个人信息</h2>
        <form action="userInfo" method="post" >
         <%--   <form action="userInfo" method="post" enctype="multipart/form-data">--%>

            <%--<label for="avatar">头像:</label>
            <div class="avatar-container">
                <img id="preview-avatar" src="images/${sessionScope.user.avatar}" alt="当前头像" class="avatar" title="点击更换头像">
                <input type="file" id="avatar-input" name="avatar" accept="image/*" style="display: none;">
                <script>
                    // 获取头像相关元素
                    const avatarElement = document.getElementById("preview-avatar");
                    const avatarInput = document.getElementById("avatar-input");

                    // 点击头像时触发文件选择器
                    avatarElement.addEventListener("click", () => {
                        avatarInput.click(); // 触发隐藏的文件选择器
                    });

                    // 监听文件选择事件，实时预览选择的图片
                    avatarInput.addEventListener("change", (event) => {
                        const file = event.target.files[0]; // 获取选中的文件

                        if (file) {
                            const reader = new FileReader(); // 创建文件读取器
                            reader.onload = function (e) {
                                // 更新头像图片为预览结果
                                avatarElement.src = e.target.result;
                            };
                            reader.readAsDataURL(file); // 读取文件为 Data URL
                            console.info("avatar:");
                        }
                    });
                </script>
            </div>--%>

            <label for="virtualName">昵称:</label>
            <input type="text" id="virtualName" name="virtualName" value="${sessionScope.user.virtualName}" required>
            <label for="birthday">生日:</label>
            <input type="date" id="birthday" name="birthday" value="${sessionScope.user.birthday}" required>
            <label for="email">邮箱:</label>
            <input type="email" id="email" name="email" value="${sessionScope.user.email}" required>
            <label for="phone">电话:</label>
            <input type="text" id="phone" name="phone" value="${sessionScope.user.phone}" required>
            <button type="submit" class="btn">保存</button>
        </form>
    </div>
</div>

<!-- 收货地址卡片 -->
<div class="address-card">
    <div class="address-header">
        <h2>收货地址</h2>
        <c:if test="${not empty requestScope.message}">
            <p class="error-message">${requestScope.message}</p>
        </c:if>
    </div>
    <div class="address-content">
        <c:forEach var="address" items="${sessionScope.user.addresses}">
            <div class="address-item">
                <div class="address-info">
                    <p><strong>收货人:</strong> ${address.receiverName}</p>
                    <p><strong>电话:</strong> ${address.receiverPhone}</p>
                    <p><strong>地址:</strong> ${address.receiverAddress}</p>
                </div>
                <button class="btn"
                        onclick="showEditAddressModal(this)"
                        data-receiver-name="${address.receiverName}"
                        data-receiver-phone="${address.receiverPhone}"
                        data-receiver-address="${address.receiverAddress}">
                    修改
                </button>
                <form action="editAddress" method="post">
                    <input type="hidden" name ="flag" value="delete">
                    <input type="hidden" name="receiverName" value="${address.receiverName}">
                    <input type="hidden" name="receiverPhone" value="${address.receiverPhone}">
                    <input type="hidden" name="receiverAddress" value="${address.receiverAddress}">
                    <button type="submit" class="btn">删除</button>
                </form>
            </div>
        </c:forEach>
        <button class="btn" onclick="showAddAddressModal()">添加收货地址</button>
    </div>
</div>

<!-- 编辑收货地址的悬浮窗口 -->
<div id="editAddressModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal('editAddressModal')">&times;</span>
        <h2>编辑收货地址</h2>
        <form action="editAddress" method="get">
            <label for="receiverName">收货人:</label>
            <input type="text" id="receiverName" name="receiverName" required>
            <label for="receiverPhone">电话:</label>
            <input type="text" id="receiverPhone" name="receiverPhone" required>
            <label for="receiverAddress">地址:</label>
            <input type="text" id="receiverAddress" name="receiverAddress" required>

            <!-- 隐藏字段传递旧值 -->
            <input type="hidden" id="oldReceiverName" name="oldReceiverName">
            <input type="hidden" id="oldReceiverPhone" name="oldReceiverPhone">
            <input type="hidden" id="oldReceiverAddress" name="oldReceiverAddress">

            <button type="submit" class="btn">保存</button>
        </form>
    </div>
</div>

<!-- 添加收货地址的悬浮窗口 -->
<div id="addAddressModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal('addAddressModal')">&times;</span>
        <h2>添加收货地址</h2>
        <form action="editAddress" method="post">
            <label for="recipient">收货人:</label>
            <input type="text" id="recipient" name="receiverName" required>
            <label for="receiverPhone1">电话:</label>
            <input type="text" id="receiverPhone1" name="receiverPhone" required>
            <label for="address">地址:</label>
            <input type="text" id="address" name="receiverAddress" required>

            <button type="submit" class="btn">保存</button>
        </form>
    </div>
</div>

<%--<!-- 添加收货地址的悬浮窗口 -->
<div id="addAddressModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal('addAddressModal')">&times;</span>
        <h2>添加收货地址</h2>

        &lt;%&ndash;<!-- 显示 AJAX 返回的错误信息 -->
        <p id="errorMessage" class="error-message" style="display:none;"></p>&ndash;%&gt;

        <form id="addAddressForm" onsubmit="submitAddressForm(event)">
            <label for="recipient">收货人:</label>
            <input type="text" id="recipient" name="receiverName" required>
            <label for="receiverPhone1">电话:</label>
            <input type="text" id="receiverPhone1" name="receiverPhone" required>
            <label for="address">地址:</label>
            <input type="text" id="address" name="receiverAddress" required>

            <button type="submit" class="btn">保存</button>
        </form>
    </div>
</div>--%>


<script>
    function showEditModal() {
        document.getElementById('editProfileModal').style.display = 'block';
    }

    function closeModal(modalId) {
        document.getElementById(modalId).style.display = 'none';
    }


    // 显示编辑地址弹窗
    function showEditAddressModal(button) {
        var receiverName = button.getAttribute('data-receiver-name');
        var receiverPhone = button.getAttribute('data-receiver-phone');
        var receiverAddress = button.getAttribute('data-receiver-address');

        // 旧值（隐藏字段）
        document.getElementById('oldReceiverName').value = receiverName;
        document.getElementById('oldReceiverPhone').value = receiverPhone;
        document.getElementById('oldReceiverAddress').value = receiverAddress;

        //新
        document.getElementById('receiverName').value = receiverName;
        document.getElementById('receiverPhone').value = receiverPhone;
        document.getElementById('receiverAddress').value = receiverAddress;

        //先赋值再显示悬浮窗口
        document.getElementById('editAddressModal').style.display = 'block';
    }

   /* function submitAddressForm(event) {
        event.preventDefault(); // 防止表单默认提交

        const form = document.getElementById('addAddressForm');
        const formData = new FormData(form);

        fetch('editAddress', {
            method: 'POST',
            body: formData
        })
            .then(response => response.text()) // 获取纯文本响应（XML）
            .then(str => (new window.DOMParser()).parseFromString(str, "application/xml"))
            .then(data => {
                const success = data.getElementsByTagName("success")[0].textContent;
                if (success === "true") {
                    // 成功，关闭窗口并刷新地址列表或更新页面元素
                    closeModal('addAddressModal');
                    // 此处可以添加代码来动态刷新页面上显示的地址信息
                } else {
                    // 显示错误信息
                    const errorMessage = document.getElementById('errorMessage');
                    errorMessage.style.display = 'block';
                    errorMessage.textContent = data.getElementsByTagName("message")[0].textContent;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }*/



    // 显示添加地址弹窗
    function showAddAddressModal() {
        document.getElementById('addAddressModal').style.display = 'block';
    }
</script>

</body>
</html>
