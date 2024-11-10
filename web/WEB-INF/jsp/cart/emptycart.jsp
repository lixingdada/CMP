<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common/top.jsp"%>

<style>
  body {
    font-family: Arial, sans-serif;
    background-color: #f7f7f7;
    color: #333;
  }

  #BackLink {
    margin: 20px;
    text-align: right;
  }

  #Catalog {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }

  #Cart h2 {
    color: #333;
    font-size: 24px;
    margin-bottom: 20px;
  }

  table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
  }

  th, td {
    padding: 12px;
    text-align: center;
    border: 1px solid #ddd;
  }

  th {
    background-color: #4CAF50;
    color: white;
  }

  tr:nth-child(even) {
    background-color: #f2f2f2;
  }

  input[type="text"] {
    width: 50px;
    text-align: center;
  }

  .Button {
    background-color: #007BFF;
    color: white;
    padding: 5px 5px; /* 增大内边距 */
    font-size: 20px; /* 增大字体大小 */
    text-decoration: none;
    border-radius: 5px;
    transition: background-color 0.3s;
  }

  .Button:hover {
    background-color: #0056b3;
  }

  /* 如果有提交按钮，可以增加相同的样式 */
  input[type="submit"] {
    background-color: #007BFF;
    color: white;
    padding: 5px 5px; /* 增大内边距 */
    font-size: 20px; /* 增大字体大小 */
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
  }

  input[type="submit"]:hover {
    background-color: #0056b3;
  }

  .centered-text {
    text-align: center;
  }
</style>

<div id="BackLink">
  <a href="mainForm">返回主菜单</a>
</div>

<div id="Catalog">
  <div id="Cart">
    <p class="centered-text">您的购物车为空，无法提交订单。</p>
    <a href="cartForm" class="Button centered-text">确定</a>
  </div>
</div>

<%@ include file="../common/bottom.jsp"%>
