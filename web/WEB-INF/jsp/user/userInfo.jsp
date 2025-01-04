<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/top.jsp" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>个人信息</title>
    <link rel="stylesheet" href="css/userInfo.css">
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>

<!-- 个人信息卡片 -->
<div class="profile-card">
    <div class="profile-header">
        <h2>个人信息</h2>
    </div>
    <div class="profile-content">
<%--        images/${sessionScope.user.avatar}--%>
        <div class="avatar-container">
            <img id="profileAvatar" src="images/${sessionScope.user.avatar != null ? sessionScope.user.avatar : '默认头像.png'}" alt="头像" class="avatar">
            <input type="file" id="avatarUpload" accept="image/*" style="display: none;">
        </div>
        <p><strong>账号: </strong>${sessionScope.user.username}</p>
        <p id="oldVirtualName"><strong>昵称: </strong>${sessionScope.user.virtualName}</p>
        <p id="oldBirthday"><strong>生日: </strong>${sessionScope.user.birthday}</p>
        <p id="oldEmail"><strong>邮箱: </strong>${sessionScope.user.email}</p>
        <p id="oldPhone"><strong>电话: </strong>${sessionScope.user.phone}</p>
        <button class="btn" data-modal ="editProfileModal">编辑</button>
    </div>
</div>

<!-- 编辑个人信息的悬浮窗口 -->
<div id="editProfileModal" class="modal">
    <div class="modal-content">
        <span class="close" id="closeEditProfileModal" data-model="editProfileModal">&times;</span>
        <h2>编辑个人信息</h2>
        <form action="userInfo" method="post" id="userInfoForm" >
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
            <button type="submit" >保存</button>
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
        <div id="addresses">
            <c:forEach var="address" items="${sessionScope.user.addresses}">
                <div class="address-item">
                    <div class="address-info">
                        <p><strong>收货人:</strong> ${address.receiverName}</p>
                        <p><strong>电话:</strong> ${address.receiverPhone}</p>
                        <p><strong>地址:</strong> ${address.receiverAddress}</p>
                    </div>
                    <button class="btn" id="editAddressButton"
                            data-modal ="editAddressModal"
                            data-receiver-name="${address.receiverName}"
                            data-receiver-phone="${address.receiverPhone}"
                            data-receiver-address="${address.receiverAddress}">
                        修改
                    </button>
                    <form id="deleteForm" action="editAddress" method="post">
                        <input type="hidden" name ="flag" value="delete">
                        <input type="hidden" name="receiverName" value="${address.receiverName}">
                        <input type="hidden" name="receiverPhone" value="${address.receiverPhone}">
                        <input type="hidden" name="receiverAddress" value="${address.receiverAddress}">
                        <button type="submit" class="btn">删除</button>
                    </form>
                </div>
            </c:forEach>
        </div>
        <button class="btn" data-modal ="addAddressModal">添加收货地址</button>
    </div>
</div>

<!-- 编辑收货地址的悬浮窗口 -->
<div id="editAddressModal" class="modal">
    <div class="modal-content">
        <span class="close" id="closeEditAddressModal">&times;</span>
        <h2>编辑收货地址</h2>
        <form id="editAddressForm" action="editAddress" method="get">
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
            <p class = "tips"></p>
            <br>
            <button type="submit" class="btn">保存</button>
        </form>
    </div>
</div>

<!-- 添加收货地址的悬浮窗口 -->
<div id="addAddressModal" class="modal">
    <div class="modal-content">
        <span class="close" id="closeAddAddressModal">&times;</span>
        <h2>添加收货地址</h2>
        <form  id="addForm" action="editAddress" method="post">
            <label for="recipient">收货人:</label>
            <input type="text" id="recipient" name="receiverName" required>
            <label for="receiverPhone1">电话:</label>
            <input type="text" id="receiverPhone1" name="receiverPhone" required>
            <label for="address">地址:</label>
            <input type="text" id="address" name="receiverAddress" required>
            <p class = "tips"></p>
            <br>
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

<script src="js/userInfo.js"></script>
</body>
</html>
