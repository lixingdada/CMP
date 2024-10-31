<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>登录与注册页面</title>
  <style>
    body {
      background-color: black;
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
      background: rgba(255, 255, 255, 0.1);
      display: flex;
      flex-direction: column;
      align-items: center;
      box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.5);
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
      width: 100%;
      margin: 10px 0;
      padding: 10px;
      border-radius: 8px;
      border: 1px solid white;
      background-color: black;
      color: white;
      text-align: left;
    }
    .input-box::placeholder {
      color: #ccc;
    }
    .submit-button {
      width: 100%;
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
  </style>
</head>
<body>

<!-- 标语 -->
<div class="header">WELCOME TO CMP!</div>

<!-- 滚动字幕条 -->
<div class="marquee">
  <span>join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) </span>
  <span>join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) join us for better coding ! :-) </span>
</div>

<div class="container">
  <!-- 照片播放墙 -->
  <div class="photo-wall">
    <img src="img/img01.png" alt="Image 1" class="active">
    <img src="img/img02.png" alt="Image 2">
    <img src="img/img03.png" alt="Image 3">
    <img src="img/img04.png" alt="Image 4">
    <img src="img/img05.png" alt="Image 5">

  </div>

  <!-- 登录注册模块 -->
  <div class="login-register">
    <!-- Menu Section -->
    <div class="menu">
      <button class="active" onclick="showLogin()">登录</button>
      <button onclick="showRegister()">注册</button>
    </div>

    <!-- Form Section -->
    <form id="form-section" action="/loginForm" method="post">
      <input type="text" class="input-box" name="username" placeholder="请输入用户名" required>
      <input type="password" class="input-box" name="password" placeholder="请输入密码" required>
      <button type="submit" class="submit-button">登录</button>
    </form>
  </div>
</div>

<script>
  // JavaScript to toggle between login and register
  function showLogin() {
    document.getElementById('submit-button').innerText = '登录';
    document.querySelectorAll('.menu button').forEach(btn => btn.classList.remove('active'));
    document.querySelector('.menu button:nth-child(1)').classList.add('active');
  }

  function showRegister() {
    document.getElementById('submit-button').innerText = '注册';
    document.querySelectorAll('.menu button').forEach(btn => btn.classList.remove('active'));
    document.querySelector('.menu button:nth-child(2)').classList.add('active');
  }

  // JavaScript to handle image slideshow
  let currentIndex = 0;
  const images = document.querySelectorAll('.photo-wall img');
  const totalImages = images.length;

  function showNextImage() {
    images[currentIndex].classList.remove('active');
    currentIndex = (currentIndex + 1) % totalImages;
    images[currentIndex].classList.add('active');
  }

  // 每2秒更换图片
  setInterval(showNextImage, 2500);
</script>

</body>
</html>
