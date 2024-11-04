<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common/top.jsp"%>

<style>
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
