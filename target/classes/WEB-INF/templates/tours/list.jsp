<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Доступные туры</h1>

    <sec:authorize access="hasAnyRole('MANAGER','ADMIN')">
        <a href="<c:url value='/tours/create'/>" class="btn btn-success">
            ➕ Новый тур
        </a>
    </sec:authorize>
</div>

<%-- Фильтры --%>
<div class="card mb-4 shadow-sm">
    <div class="card-body">
        <form id="filterForm" class="row g-3">
            <div class="col-md-4">
                <label class="form-label">Дата начала от:</label>
                <input type="date" class="form-control" id="startDate" name="startDate">
            </div>
            <div class="col-md-4">
                <label class="form-label">Дата окончания до:</label>
                <input type="date" class="form-control" id="endDate" name="endDate">
            </div>
            <div class="col-md-4 d-flex align-items-end">
                <button type="submit" class="btn btn-primary w-100">🔍 Фильтровать</button>
            </div>
        </form>
    </div>
</div>

<%-- Сетка туров --%>
<div id="toursList" class="row g-4">
    <c:forEach var="tour" items="${tours}">
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm hover-shadow">
                <div class="card-body">
                    <h5 class="card-title">${tour.title}</h5>
                    <p class="card-text text-muted small">
                        📅 ${tour.startDate} -
                        ${tour.endDate}
                    </p>
                    <p class="card-text">${tour.description}</p>

                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <span class="badge bg-primary">${tour.excursions.size()} экскурсий</span>
                        <c:if test="${not empty tour.basePrice}">
                            <span class="fw-bold price" data-rub="${tour.basePrice}">от ${tour.basePrice} ₽</span>
                        </c:if>
                    </div>
                </div>
                <div class="card-footer bg-white border-top-0">
                    <div class="d-grid gap-2">
                        <a href="<c:url value='/tours/${tour.id}'/>" class="btn btn-outline-primary">
                            Подробнее
                        </a>

                        <sec:authorize access="isAuthenticated()">
                            <form method="post" action="<c:url value='/orders/add-tour'/>">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <input type="hidden" name="tourId" value="${tour.id}"/>
                                <button class="btn btn-primary w-100">🛒 В корзину</button>
                            </form>
                        </sec:authorize>

                        <sec:authorize access="hasAnyRole('MANAGER','ADMIN')">
                            <div class="btn-group w-100 mt-2">
                                <a href="<c:url value='/tours/${tour.id}/edit'/>"
                                   class="btn btn-sm btn-outline-secondary">✏️</a>
                                <button class="btn btn-sm btn-outline-danger"
                                        onclick="deleteTour(${tour.id})">🗑️</button>
                            </div>
                        </sec:authorize>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<%-- Пустое состояние --%>
<c:if test="${empty tours}">
    <div class="text-center py-5">
        <h4 class="text-muted">😔 Туры не найдены</h4>
        <p class="text-muted">Попробуйте изменить параметры фильтрации</p>
    </div>
</c:if>

<%-- Селектор валют --%>
<div class="mt-4 text-end">
    <label class="me-2">Показать цены в:</label>
    <select id="currencySelector" class="form-select d-inline-block w-auto" onchange="updateCurrency()">
        <option value="RUB">RUB ₽</option>
        <option value="USD">USD $</option>
        <option value="EUR">EUR €</option>
    </select>
</div>

<script>
    // Фильтрация туров через AJAX
    document.getElementById('filterForm').addEventListener('submit', function(e) {
        e.preventDefault();
        filterTours();
    });

    function filterTours() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        fetch(`<c:url value='/tours/filter'/>?startDate=${startDate}&endDate=${endDate}`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        })
        .then(response => response.text())
        .then(html => {
            document.getElementById('toursList').innerHTML = html;
        })
        .catch(error => {
            showNotification('Ошибка при фильтрации', 'error');
            console.error('Filter error:', error);
        });
    }

    // Добавление тура в корзину выполняется обычной POST-формой выше.



    // Удаление тура (менеджер)
    function deleteTour(tourId) {
        if (!confirm('Вы уверены, что хотите отменить этот тур?')) return;

        fetch(`<c:url value="/tours/"/>${tourId}/delete`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                showNotification('Ошибка при удалении', 'error');
            }
        });
    }

    // Конвертация валют выполняется глобальной функцией из app.js

    // Показ уведомления
    function showNotification(message, type) {
        const container = document.getElementById('notification-area');
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        container.appendChild(alert);
        setTimeout(() => alert.remove(), 5000);
    }
</script>
<jsp:include page="/WEB-INF/templates/pagination.jsp" />
