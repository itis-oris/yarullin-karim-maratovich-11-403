<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h2>История заказов</h2>
<c:choose>
    <c:when test="${empty orders}">
        <div class="alert alert-info">История пока пуста.</div>
    </c:when>
    <c:otherwise>
        <table class="table table-striped align-middle">
            <tr><th>Дата</th><th>Тур</th><th>Статус</th><th>Сумма</th></tr>
            <c:forEach var="order" items="${orders}">
                <tr>
                    <td>${order.createdAt}</td>
                    <td><a href="<c:url value='/tours/${order.tour.id}'/>">${order.tour.title}</a></td>
                    <td>${order.status}</td>
                    <td><span class="price" data-rub="${order.totalPrice}">${order.totalPrice} ₽</span></td>
                </tr>
            </c:forEach>
        </table>
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
