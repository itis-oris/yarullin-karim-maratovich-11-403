<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h2>Корзина</h2>
<c:choose>
    <c:when test="${empty orders}">
        <div class="alert alert-info">Корзина пуста. <a href="<c:url value='/tours'/>">Выберите тур</a>.</div>
    </c:when>
    <c:otherwise>
        <table class="table table-striped align-middle">
            <tr><th>Тур</th><th>Статус</th><th>Сумма</th><th></th></tr>
            <c:forEach var="order" items="${orders}">
                <tr>
                    <td><a href="<c:url value='/tours/${order.tour.id}'/>">${order.tour.title}</a></td>
                    <td>${order.status}</td>
                    <td><span class="price" data-rub="${order.totalPrice}">${order.totalPrice} ₽</span></td>
                    <td class="text-end">
                        <form method="post" action="<c:url value='/orders/${order.id}/remove'/>" class="d-inline">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-sm btn-outline-danger">Удалить</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form method="post" action="<c:url value='/orders/checkout'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button class="btn btn-success">Оформить заказ</button>
            <a class="btn btn-outline-secondary" href="<c:url value='/tours'/>">Продолжить выбор</a>
        </form>
    </c:otherwise>
</c:choose>


<div class="mt-4 text-end">
    <label class="me-2">Показать цены в:</label>
    <select id="currencySelector" class="form-select d-inline-block w-auto" onchange="updateCurrency()">
        <option value="RUB">RUB ₽</option>
        <option value="USD">USD $</option>
        <option value="EUR">EUR €</option>
    </select>
</div>

<jsp:include page="/WEB-INF/templates/pagination.jsp" />
