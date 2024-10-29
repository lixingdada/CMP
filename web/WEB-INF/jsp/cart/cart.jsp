<%@ include file="../common/top.jsp"%>

<div id="BackLink"><stripes:link
        beanclass="org.mybatis.jpetstore.web.actions.CatalogActionBean">
    Return to Main Menu</stripes:link></div>

<div id="Catalog">

    <div id="Cart">

        <h2>Shopping Cart</h2>

        <form action="" method="post">
            <table>
                <tr>
                    <th><b>Item ID</b></th>
                    <th><b>Product ID</b></th>
                    <th><b>Description</b></th>
                    <th><b>Quantity</b></th>
                    <th><b>List Price</b></th>
                    <th><b>Total Cost</b></th>
                    <th>&nbsp;</th>
                </tr>

                <c:if test="${sessionScope.cart.numberOfItems == 0}">
                    <tr>
                        <td colspan="8"><b>Your cart is empty.</b></td>
                    </tr>
                </c:if>

                <c:forEach var="cartItem" items="${sessionScope.cart.cartItems}">
                    <tr>
                        <td>
                            <a href="itemForm?itemId=${cartItem.item.itemId}">${cartItem.item.itemId}</a>
                        </td>

                        <td>${cartItem.item.product.productId}</td>
                        <td>${cartItem.item.attribute1} ${cartItem.item.attribute2}
                                ${cartItem.item.attribute3} ${cartItem.item.attribute4}
                                ${cartItem.item.attribute5} ${cartItem.item.product.name}</td>

                        <td>
                            <input type="text" name="${cartItem.item.itemId}" value="${cartItem.quantity}" >
                        </td>

                        <td><fmt:formatNumber value="${cartItem.item.listPrice}"
                                              pattern="$#,##0.00" /></td>
                        <td><fmt:formatNumber value="${cartItem.total}"
                                              pattern="$#,##0.00" /></td>

                        <td>
                            <a href="removeItemFromCart?cartItem=${cartItem.item.itemId}" class="Button" >Remove</a>
                        </td>
                    </tr>
                </c:forEach>

                <tr>
                    <td colspan="7">
                        Sub Total: <fmt:formatNumber value="${sessionScope.cart.subTotal}" pattern="$#,##0.00" />
                        <input type="submit" value="Update Cart">
                    </td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </form>

        <c:if test="${sessionScope.cart.numberOfItems > 0}">
            <a href="newOrderForm" class="Button">Proceed to Checkout</a>
        </c:if>
    </div>

    <div id="Separator">&nbsp;</div>
</div>

<%@ include file="../common/bottom.jsp"%>