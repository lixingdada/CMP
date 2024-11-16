
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>登录与注册页面</title>
    <style>
        body {
            background-image: url('${pageContext.request.contextPath}/images/imgback.png'); /* 添加背景图片 */
            background-size: cover; /* 背景图片自适应屏幕 */
            background-position: center; /* 背景图片居中 */
            background-repeat: no-repeat; /* 防止背景图片重复 */
            color: white;
            font-family: Arial, sans-serif;
            font-size: 30px;
            margin: 0;
            display: flex;
            justify-content: space-around;
            align-items: center;
            height: 100vh;
            flex-direction: column;
            overflow: hidden; /* 防止滚动条 */
        }
        .header {
            width: 100%;
            text-align: center;
            font-size: 80px;
            color: white;
            margin-bottom: 20px;
            font-weight: 400;
        }
        .marquee {
            position: absolute;
            bottom: 500px; /* 设置字幕条距离底部的距离 */
            width: 100%;
            white-space: nowrap; /* 不换行 */
            overflow: hidden; /* 隐藏超出部分 */
        }
        .marquee span {
            display: inline-block;
            padding-left: 100%; /* 使其从右侧开始 */
            animation: marquee 30s linear infinite; /* 动画 */
        }
        @keyframes marquee {
            0% { transform: translate(0, 0); }
            100% { transform: translate(-90%, 0); }
        }
        .container {
            display: flex;
            justify-content: space-between;
            width: 80%;
        }
        /* 左侧的照片播放墙 */
        .photo-wall {
            width: 60%;
            height: 400px;
            border-radius: 15px;
            overflow: hidden;
            background-color: white;
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
            box-shadow: 0px 8px 20px rgba(0, 0, 0, 0.5); /* 添加阴影效果 */
        }
        .photo-wall img {
            width: 100%;
            height: 100%;
            position: absolute;
            opacity: 0;
            transition: opacity 1s ease-in-out;
        }
        .photo-wall img.active {
            opacity: 1;
        }
        .photo-wall img:hover {
            transform: scale(1.1); /* 略微增大 */
            opacity: 0.8; /* 降低透明度 */
            transition: transform 0.3s ease, opacity 0.3s ease; /* 添加过渡效果 */
        }
        /* 右侧的登录注册模块 */
        .login-register {
            width: 30%;
            padding: 20px;
            border-radius: 15px;
            background: rgba(0, 0, 0, 0.4); /* 设置为带透明度的黑色背景 */
            display: flex;
            flex-direction: column;
            align-items: center;
            box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.7); /* 添加阴影 */
            color: black;
        }

        .menu {
            display: flex;
            width: 100%;
            justify-content: space-around;
            margin-bottom: 20px;
        }
        .menu button {
            background: none;
            border: none;
            color: white;
            font-size: 18px;
            cursor: pointer;
            padding: 10px;
            flex: 1;
        }
        .menu button.active {
            border-bottom: 2px solid white;
        }
        .input-box {
            width: 90%;
            margin: 10px 0;
            padding: 10px;
            border-radius: 8px;
            border: 1px solid white;
            background-color: rgba(255, 255, 255, 1); /* 设置带有透明度的白色背景 */
            color: black; /* 修改文本颜色为黑色，以保证在浅色背景上清晰可见 */
            text-align: left;
        }
        .input-box::placeholder {
            color: #666; /* 修改占位符颜色为更深的灰色 */
        }

        .submit-button {
            width: 95.5%;
            padding: 10px;
            font-size: 16px;
            color: black;
            background-color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 20px;
        }
        .submit-button:active {
            background-color: lightblue; /* 按下时变为灰色 */
            color: black; /* 按下时文本颜色变为黑色 */
        }

        .login-register {
            position: relative; /* 设置为相对定位，用于定位错误信息 */
        }
        .error-message {
            color: #d8000c; /* 红色字体 */
            font-weight: bold; /* 粗体显示 */
            font-size: 0.5em; /* 字体大小 */
            padding: 5px; /* 内边距 */
            background-color: transparent; /* 背景色设置为无色 */
            display: flex; /* 使用 flexbox */
            align-items: center; /* 垂直居中对齐 */
            position: absolute; /* 绝对定位，悬浮在表单上方 */
            top: 550px; /* 向上移动错误信息，您可以根据需要调整这个值 */
            left: 83%; /* 水平居中 */
            transform: translateX(-50%); /* 水平居中偏移 */
            text-align: left; /* 左对齐文本 */
            z-index: 1000; /* 确保错误信息在最上层 */
            width: 10%; /* 调整为更宽的错误框 */
            /* box-shadow: none; 取消阴影 */
        }

        .success-message {
            color: white; /* 白色字体 */
            font-weight: bold; /* 粗体显示 */
            font-size: 0.5em; /* 字体大小 */
            padding: 5px; /* 内边距 */
            background-color: transparent; /* 背景色设置为无色 */
            display: flex; /* 使用 flexbox */
            align-items: center; /* 垂直居中对齐 */
            position: absolute; /* 绝对定位，悬浮在表单上方 */
            top: 550px; /* 向上移动错误信息，您可以根据需要调整这个值 */
            left: 83%; /* 水平居中 */
            transform: translateX(-50%); /* 水平居中偏移 */
            text-align: left; /* 左对齐文本 */
            z-index: 1000; /* 确保错误信息在最上层 */
            width: 10%; /* 调整为更宽的错误框 */
            /* box-shadow: none; 取消阴影 */
        }



    </style>
</head>
<body>

<!-- 标语 -->
<div class="header">WELCOME TO CMP!</div>

<!-- 滚动字幕条 -->
<%--<div class="marquee">--%>
<%--    <span>join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) </span>--%>
<%--    <span>join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) </span>--%>
<%--</div>--%>

<div class="container">
    <!-- 照片播放墙 -->
    <div class="photo-wall">
        <img src="${pageContext.request.contextPath}/images/img01.png" alt="Image 1" class="active">
        <img src="${pageContext.request.contextPath}/images/img02.png" alt="Image 2">
        <img src="${pageContext.request.contextPath}/images/img03.png" alt="Image 3">
        <img src="${pageContext.request.contextPath}/images/img04.png" alt="Image 4">
        <img src="${pageContext.request.contextPath}/images/img05.png" alt="Image 5">
    </div>

<%--<<<<<<< HEAD
    <!-- Form Section -->
    <form id="form-section" action="loginForm" method="post">
      <input type="text" class="input-box" name="username" placeholder="请输入用户名" required>
      <input type="password" class="input-box" name="password" placeholder="请输入密码" required>
      <button type="submit" class="submit-button">登录</button>
    </form>
  </div>
=======--%>
    <!-- 登录注册模块 -->

    <c:if test="${not empty successMessage}">
    <div class="success-message">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errorMessage}">
    <div class="error-message">${errorMessage}</div>
    </c:if>

    <div class="login-register">
        <!-- Menu Section -->
        <div class="menu">
            <button class="active" onclick="showLogin()">登录</button>
            <button onclick="showRegister()">注册</button>
        </div>

        <!-- 登录表单 -->
        <form id="login-form" action="loginForm" method="post">
            <input type="text" class="input-box" name="username" placeholder="请输入用户名" required>
            <input type="password" class="input-box" name="password" placeholder="请输入密码" required>

            <!-- 验证码输入框和图片 -->
            <input type="text" class="input-box" name="captcha" placeholder="请输入验证码" required>
            <img id="validateimg" src="captchaServlet" onclick="clickrefresh()" alt="验证码" title="点击刷新验证码" style="cursor: pointer;">

            <button type="submit" class="submit-button" id="login-button">登录</button>
        </form>

        <!-- 注册表单 -->
        <form id="register-form" action="registerForm" method="post" style="display: none;">
            <input type="text" class="input-box" name="newUsername" placeholder="请输入用户名" required>
            <input type="password" class="input-box" name="newPassword" placeholder="请输入密码" required>
            <button type="submit" class="submit-button" id="register-button">注册</button>
        </form>
    </div>

    <script>
        // 切换显示登录和注册表单
        // function showLogin() {
        //
        //     document.getElementById("login-form").style.display = "block";
        //     document.getElementById("register-form").style.display = "none";
        // }
        //
        // function showRegister() {
        //     document.getElementById("register-form").style.display = "block";
        //     document.getElementById("login-form").style.display = "none";
        // }

        // 刷新验证码
        function clickrefresh() {
            const validateImg = document.getElementById("validateimg");
            validateImg.src = "captchaServlet?" + new Date().getTime();
        }
    </script>



<%--<c:if test="${not empty errorMessage}">--%>
<%--    <div>${errorMessage}</div>--%>
<%--</c:if>--%>

<script>
    // JavaScript to toggle between login and register
    function showLogin() {
        document.getElementById('login-form').style.display = 'block';  // 显示登录表单
        document.getElementById('register-form').style.display = 'none'; // 隐藏注册表单
        document.querySelectorAll('.menu button').forEach(btn => btn.classList.remove('active'));
        document.querySelector('.menu button:nth-child(1)').classList.add('active');
    }

    function showRegister() {
        document.getElementById('login-form').style.display = 'none';   // 隐藏登录表单
        document.getElementById('register-form').style.display = 'block'; // 显示注册表单
        document.querySelectorAll('.menu button').forEach(btn => btn.classList.remove('active'));
        document.querySelector('.menu button:nth-child(2)').classList.add('active');
    }

    // 确保 DOM 加载完成后再执行 JavaScript
    window.onload = function() {
        let currentIndex = 0;
        const images = document.querySelectorAll('.photo-wall img');
        const totalImages = images.length;

        // 显示下一张图片
        function showNextImage() {
            images[currentIndex].classList.remove('active');
            currentIndex = (currentIndex + 1) % totalImages;
            images[currentIndex].classList.add('active');
        }

        // 每2.5秒更换图片
        setInterval(showNextImage, 2500);
    };
</script>

</body>
</html>
