
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <meta charset="UTF-8">
    <title>登录与注册页面</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/icon.png" type="image/x-icon">
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
            justify-content: center; /* 水平居中 */
            align-items: center; /* 垂直居中 */
            height: 100vh; /* 使 body 高度为 100% */
            overflow: hidden; /* 防止滚动条 */
            flex-direction: column;
        }

        .header {
            width: 100%;
            text-align: center;
            font-size: 80px;
            color: white;
            font-weight: 400;
            margin-bottom: 70px;
        }

        .visit {
            position: relative;
            top: 0px;
            width: 100%;
            text-align: center;
            font-size: 30px;
            color: white;
            margin: 0;
            text-decoration: none;
            transition: color 0.3s ease;
        }

        .visit:hover {
            color: black;
        }

        .container {
            display: flex;
            justify-content: center; /* 水平居中 */
            align-items: center; /* 垂直居中 */
            flex-wrap: wrap;
            width: 100%;
            max-width: 1200px; /* 限制最大宽度 */
            margin-top: 50px;
        }

        /* 照片播放墙 */
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
            box-shadow: 0px 8px 20px rgba(0, 0, 0, 0.5);
            margin-right: 76px
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
            transform: scale(1.1);
            opacity: 0.8;
            transition: transform 0.3s ease, opacity 0.3s ease;
        }

        /* 登录注册模块 */
        .login-register {
            width: 30%;
            padding: 20px;
            border-radius: 15px;
            background: rgba(0, 0, 0, 0.4);
            display: flex;
            flex-direction: column;
            align-items: center;
            box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.7);
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
            background-color: rgba(255, 255, 255, 1);
            color: black;
            text-align: left;
        }

        .input-box::placeholder {
            color: #666;
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
            background-color: lightblue;
            color: black;
        }

        .login-register {
            position: relative;
        }

        .message {
            display: none;
            margin-bottom: 0px;
            padding: 0px;
            border-radius: 4px;
            text-align: center;
            font-size: 20px;
            z-index: 1000;
            width: 100%;
        }

        .message.error {
            color: #d8000c;
            background-color: rgb(255,255,255,0);
        }

        .message.success {
            color: WHITE;
            background-color: rgb(255,255,255,0);
        }

        .agree {
            font-size: 15px;
            text-decoration: none;
        }
    </style>

</head>
<body>

<!-- 标语 -->
<div class="header">WELCOME TO CMP!</div>
<a href="main" class="visit"> 游 客 访 问 </a>
<div class="container">
    <!-- 照片播放墙 -->
    <div class="photo-wall">
        <img src="${pageContext.request.contextPath}/images/img01.png" alt="Image 1" class="active">
        <img src="${pageContext.request.contextPath}/images/img02.png" alt="Image 2">
        <img src="${pageContext.request.contextPath}/images/img03.png" alt="Image 3">
        <img src="${pageContext.request.contextPath}/images/img04.png" alt="Image 4">
        <img src="${pageContext.request.contextPath}/images/img05.png" alt="Image 5">
    </div>


    <!-- login-register.html -->
    <div class="login-register">
        <!-- Menu Section -->
        <div class="menu">
            <button class="active" onclick="showLogin()">登录</button>
            <button onclick="showRegister()">注册</button>
        </div>

        <!-- 登录表单 -->
        <form id="login-form">
            <div id="login-message" class="message"></div> <!-- 消息显示容器 -->
            <input type="text" class="input-box" name="username" placeholder="请输入用户名" required>
            <input type="password" class="input-box" name="password" placeholder="请输入密码" required>

            <!-- 验证码输入框和图片 -->
            <input type="text" class="input-box" name="captcha" placeholder="请输入验证码" required>
            <img id="validateimg" src="captchaServlet" onclick="clickrefresh()" alt="验证码" title="点击刷新验证码" style="cursor: pointer;">

            <br>
            <label class="agree">
                <input type="radio">
                我已经阅读并且知晓
                <a href="#" target="_blank">&laquo;用户须知手册&raquo;</a>
                与
                <a href="#" target="_blank">&laquo;用户安全协议&raquo;</a>
            </label>
            <button type="submit" class="submit-button" id="login-button">登录</button>
        </form>

        <!-- 注册表单 -->
        <form id="register-form" style="display: none;">
            <div id="register-message" class="message"></div> <!-- 消息显示容器 -->
            <input type="text" class="input-box" name="newUsername" placeholder="请输入用户名" required>
            <input type="password" class="input-box" name="newPassword" placeholder="请输入密码" required>
            <button type="submit" class="submit-button" id="register-button">注册</button>
        </form>
    </div>


    <script>


        // 刷新验证码
        function clickrefresh() {
            const validateImg = document.getElementById("validateimg");
            validateImg.src = "captchaServlet?" + new Date().getTime();
        }
    </script>





    <script>

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
    <script>
        // personal.js
        // personal.js
        $(document).ready(function() {
            // 切换表单视图
            window.showLogin = function() {
                $('#login-form').show();
                $('#register-form').hide();
                $('.menu button').removeClass('active');
                $('.menu button').eq(0).addClass('active');
                clearMessages();
            }

            window.showRegister = function() {
                $('#login-form').hide();
                $('#register-form').show();
                $('.menu button').removeClass('active');
                $('.menu button').eq(1).addClass('active');
                clearMessages();
            }


            function clearMessages() {
                $('.message').hide().removeClass('success error').text('');
            }

            // 使用对象存储每个消息容器的计时器
            let messageTimeouts = {};

            // 函数用于显示消息并自动隐藏
            function showMessage(element, type, message, duration = 3000) {
                let elementId = element.attr('id');

                // 移除之前的类
                element.removeClass('success error');

                // 添加新的类
                element.addClass(type);

                // 设置消息文本
                element.text(message);

                // 显示消息
                element.fadeIn(); // 使用 fadeIn() 增加显示时的过渡效果

                // 清除之前的计时器
                if (messageTimeouts[elementId]) {
                    clearTimeout(messageTimeouts[elementId]);
                }

                // 设置新的计时器
                messageTimeouts[elementId] = setTimeout(function() {
                    element.fadeOut(); // 使用 fadeOut() 增加隐藏时的过渡效果
                    delete messageTimeouts[elementId];
                }, duration);
            }
            // // 处理用户名输入框失去焦点事件
            // $('#newUsername').blur(function() {
            //     let username = $(this).val().trim();
            //     let errorElement = $('#username-error');
            //
            //     if (username === '') {
            //         showFieldError($(this), '用户名不能为空。');
            //         return;
            //     }
            // 处理登录表单提交
            $('#login-form').submit(function(event) {
                event.preventDefault(); // 阻止默认表单提交

                clearMessages(); // 清除之前的消息

                var formData = {
                    username: $('input[name="username"]').val(),
                    password: $('input[name="password"]').val(),
                    captcha: $('input[name="captcha"]').val()
                };

                $.ajax({
                    url: 'loginForm',
                    type: 'POST',
                    data: formData,
                    dataType: 'json',
                    success: function(response) {
                        console.log("Login Response:", response);
                        if (response.success) {
                            showMessage($('#login-message'), 'success', response.message, 3000);
                            // 如果有重定向地址，则跳转
                            if (response.redirect) {
                                window.location.href = response.redirect;
                            }
                        } else {
                            showMessage($('#login-message'), 'error', response.message, 3000);
                            // 刷新验证码
                            $('#validateimg').click();
                        }
                    },
                    error: function(xhr, status, error) {
                        showMessage($('#login-message'), 'error', "服务器错误，请稍后重试。", 3000);
                    }
                });
            });

            // 处理注册表单提交
            $('#register-form').submit(function(event) {
                event.preventDefault(); // 阻止默认表单提交

                clearMessages(); // 清除之前的消息

                var formData = {
                    newUsername: $('input[name="newUsername"]').val(),
                    newPassword: $('input[name="newPassword"]').val()
                };

                $.ajax({
                    url: 'registerForm',
                    type: 'POST',
                    data: formData,
                    dataType: 'json',
                    success: function(response) {
                        if (response.success) {
                            showMessage($('#register-message'), 'success', response.message, 3000);
                            //如果有重定向地址，则跳转
                            // if (response.redirect) {
                            //     window.location.href = response.redirect;
                            // }
                        } else {
                            showMessage($('#register-message'), 'error', response.message, 3000);
                        }
                    },
                    error: function(xhr, status, error) {
                        showMessage($('#register-message'), 'error', "服务器错误，请稍后重试", 3000);
                    }
                });
            });
// 添加用户名输入框失去焦点时的验证
            $('input[name="newUsername"]').blur(function() {
                var username = $(this).val().trim();

                // 如果用户名为空，则不进行验证
                if (username === "") {
                    showMessage($('#register-message'), 'error', "用户名不能为空。", 3000);
                    return;
                }

                $.ajax({
                    url: 'registerForm', // 后端用于检查用户名是否存在的接口
                    type: 'GET', // 或者 'POST'，根据后端接口要求
                    data: { username: username },
                    dataType: 'json',
                    success: function(response) {
                        if (response.exists) {
                            showMessage($('#register-message'), 'error', "⚠ 用户名已存在，请选择其他用户名", 3000);
                            // 可选：禁用注册按钮以防止提交
                            $('#register-button').prop('disabled', true);
                        } else {
                            showMessage($('#register-message'), 'success', "😊用户名可用", 3000);
                            // 可选：启用注册按钮
                            $('#register-button').prop('disabled', false);
                        }
                    },
                    error: function(xhr, status, error) {
                        showMessage($('#register-message'), 'error', "验证用户名时出错，请稍后重试", 3000);
                    }
                });
            });

            // 可选：在输入用户名时清除消息并启用注册按钮
            $('input[name="newUsername"]').on('input', function() {
                clearMessages();
                $('#register-button').prop('disabled', false);
            });

            // 刷新验证码函数
            window.clickrefresh = function() {
                $('#validateimg').attr('src', 'captchaServlet?rand=' + Math.random());
            }
        });

    </script>
</div>
</body>
</html>
