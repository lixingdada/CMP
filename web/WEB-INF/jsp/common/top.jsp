<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>

<html>

<head>
    <title>CMP</title>
    <link rel="StyleSheet" href="css/1.css" type="text/css"
          media="screen" />
</head>

<body>

<div id="Header">

    <div id="Logo">
        <div id="LogoContent">
            <a href="mainForm"><img src="../images/return.png"></a>
        </div>
    </div>


                            <%--  待修改  --%>


<%--    <div id="Menu">--%>
<%--        <div id="MenuContent"><stripes:link--%>
<%--                beanclass="org.mybatis.jpetstore.web.actions.CartActionBean"--%>
<%--                event="viewCart">--%>
<%--            <img align="middle" name="img_cart" src="../images/cart.gif" />--%>
<%--        </stripes:link> <img align="middle" src="../images/separator.gif" /> <c:if--%>
<%--                test="${sessionScope.accountBean == null}">--%>
<%--            <stripes:link--%>
<%--                    beanclass="org.mybatis.jpetstore.web.actions.AccountActionBean"--%>
<%--                    event="signonForm">--%>
<%--                Sign In--%>
<%--            </stripes:link>--%>
<%--        </c:if> <c:if test="${sessionScope.accountBean != null}">--%>
<%--            <c:if test="${!sessionScope.accountBean.authenticated}">--%>
<%--                <stripes:link--%>
<%--                        beanclass="org.mybatis.jpetstore.web.actions.AccountActionBean"--%>
<%--                        event="signonForm">--%>
<%--                    Sign In--%>
<%--                </stripes:link>--%>
<%--            </c:if>--%>
<%--        </c:if> <c:if test="${sessionScope.accountBean != null}">--%>
<%--            <c:if test="${sessionScope.accountBean.authenticated}">--%>
<%--                <stripes:link--%>
<%--                        beanclass="org.mybatis.jpetstore.web.actions.AccountActionBean"--%>
<%--                        event="signoff">--%>
<%--                    Sign Out--%>
<%--                </stripes:link>--%>
<%--                <img align="middle" src="../images/separator.gif" />--%>
<%--                <stripes:link--%>
<%--                        beanclass="org.mybatis.jpetstore.web.actions.AccountActionBean"--%>
<%--                        event="editAccountForm">--%>
<%--                    My Account--%>
<%--                </stripes:link>--%>
<%--            </c:if>--%>
<%--        </c:if> <img align="middle" src="../images/separator.gif" /> <a--%>
<%--                href="../help.html">?</a></div>--%>
<%--    </div>--%>

<%--    <div id="Search">--%>
<%--        <div id="SearchContent"><stripes:form--%>
<%--                beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean">--%>
<%--            <stripes:text name="keyword" size="14" />--%>
<%--            <stripes:submit name="searchProducts" value="Search" />--%>
<%--        </stripes:form></div>--%>
<%--    </div>--%>

<%--    <div id="QuickLinks"><stripes:link--%>
<%--            beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean"--%>
<%--            event="viewCategory">--%>
<%--        <stripes:param name="categoryId" value="FISH" />--%>
<%--        <img src="../images/sm_fish.gif" />--%>
<%--    </stripes:link> <img src="../images/separator.gif" /> <stripes:link--%>
<%--            beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean"--%>
<%--            event="viewCategory">--%>
<%--        <stripes:param name="categoryId" value="DOGS" />--%>
<%--        <img src="../images/sm_dogs.gif" />--%>
<%--    </stripes:link> <img src="../images/separator.gif" /> <stripes:link--%>
<%--            beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean"--%>
<%--            event="viewCategory">--%>
<%--        <stripes:param name="categoryId" value="REPTILES" />--%>
<%--        <img src="../images/sm_reptiles.gif" />--%>
<%--    </stripes:link> <img src="../images/separator.gif" /> <stripes:link--%>
<%--            beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean"--%>
<%--            event="viewCategory">--%>
<%--        <stripes:param name="categoryId" value="CATS" />--%>
<%--        <img src="../images/sm_cats.gif" />--%>
<%--    </stripes:link> <img src="../images/separator.gif" /> <stripes:link--%>
<%--            beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean"--%>
<%--            event="viewCategory">--%>
<%--        <stripes:param name="categoryId" value="BIRDS" />--%>
<%--        <img src="../images/sm_birds.gif" />--%>
<%--    </stripes:link></div>--%>

<%--</div>--%>

<%--<div id="Content"><stripes:messages />--%>