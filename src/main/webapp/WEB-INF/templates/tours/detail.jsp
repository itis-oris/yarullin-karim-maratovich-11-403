<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="card shadow-sm mb-4">
    <div class="card-body">
        <div class="d-flex justify-content-between align-items-start gap-3">
            <div>
                <h1 class="mb-2">${tour.title}</h1>
                <p class="text-muted mb-2">📅 ${tour.startDate} — ${tour.endDate}</p>
                <p>${tour.description}</p>
            </div>
            <c:if test="${not empty tour.basePrice}">
                <div class="h4 text-nowrap price" data-rub="${tour.basePrice}">${tour.basePrice} ₽</div>
            </c:if>
        </div>

        <c:if test="${not empty tour.hotelName}">
            <hr/>
            <h5>Отель</h5>
            <p class="mb-0">🏨 ${tour.hotelName} — ${tour.hotelCity}, ${tour.hotelStars}★</p>
        </c:if>
    </div>
</div>

<div class="card shadow-sm mb-4">
    <div class="card-body">
        <h4>Экскурсии в туре</h4>
        <c:choose>
            <c:when test="${empty tour.scheduledExcursions}">
                <p class="text-muted mb-0">Экскурсии ещё не добавлены.</p>
            </c:when>
            <c:otherwise>
                <ol class="list-group list-group-numbered">
                    <c:forEach var="item" items="${tour.scheduledExcursions}">
                        <li class="list-group-item d-flex justify-content-between align-items-start">
                            <div>
                                <a href="<c:url value='/excursions/${item.excursion.id}'/>">${item.excursion.title}</a>
                                <div class="small text-muted">${item.startTime}–${item.endTime}, ${item.excursion.durationMinutes} мин</div>
                                <div>${item.excursion.description}</div>
                            </div>
                            <span class="badge bg-primary rounded-pill price" data-rub="${item.excursion.price}">${item.excursion.price} ₽</span>
                        </li>
                    </c:forEach>
                </ol>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="d-flex gap-2">
    <a href="<c:url value='/tours'/>" class="btn btn-outline-secondary">Назад к турам</a>
    <sec:authorize access="isAuthenticated()">
        <form method="post" action="<c:url value='/orders/add-tour'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="tourId" value="${tour.id}"/>
            <button class="btn btn-primary">Добавить в корзину</button>
        </form>
    </sec:authorize>
</div>


<div class="mt-4 text-end">
    <label class="me-2">Показать цены в:</label>
    <select id="currencySelector" class="form-select d-inline-block w-auto" onchange="updateCurrency()">
        <option value="RUB">RUB ₽</option>
        <option value="USD">USD $</option>
        <option value="EUR">EUR €</option>
    </select>
</div>
