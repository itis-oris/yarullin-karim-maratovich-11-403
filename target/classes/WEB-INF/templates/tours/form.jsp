<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h2>${tour.id == null ? 'Создание тура' : 'Редактирование тура'}</h2>

<c:if test="${not empty errorMessage}"><div class="alert alert-danger">${errorMessage}</div></c:if>
<c:if test="${not empty errors}">
    <div class="alert alert-danger"><ul class="mb-0"><c:forEach var="e" items="${errors}"><li>${e}</li></c:forEach></ul></div>
</c:if>

<c:choose>
    <c:when test="${tour.id == null}"><c:set var="actionUrl" value="/tours/create"/></c:when>
    <c:otherwise><c:set var="actionUrl" value="/tours/${tour.id}/edit"/></c:otherwise>
</c:choose>

<form method="post" action="<c:url value='${actionUrl}'/>">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <h5 class="card-title">Основная информация</h5>
            <input class="form-control mb-2" name="title" value="${tour.title}" placeholder="Название" required/>
            <textarea class="form-control mb-2" name="description" placeholder="Описание" rows="3">${tour.description}</textarea>
            <div class="row g-2">
                <div class="col-md-4"><input class="form-control" type="date" name="startDate" value="${tour.startDate}" required/></div>
                <div class="col-md-4"><input class="form-control" type="date" name="endDate" value="${tour.endDate}" required/></div>
                <div class="col-md-4"><input class="form-control" type="number" step="0.01" name="basePrice" value="${tour.basePrice}" placeholder="Базовая цена"/></div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <h5 class="card-title">Отель</h5>
            <select class="form-select" name="hotelId">
                <option value="">Без отеля</option>
                <c:forEach var="hotel" items="${hotels}">
                    <option value="${hotel.id}" ${hotel.id == tour.hotelId ? 'selected' : ''}>${hotel.name} — ${hotel.city}, ${hotel.stars}★</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="card-title mb-0">Экскурсии по порядку</h5>
                <button type="button" class="btn btn-sm btn-outline-primary" onclick="addExcursionRow()">Добавить экскурсию</button>
            </div>
            <p class="text-muted small">Порядок строк задаёт порядок экскурсий в туре. Время окончания считается автоматически по длительности экскурсии.</p>
            <div id="excursionRows">
                <c:forEach var="scheduled" items="${tour.scheduledExcursions}">
                    <div class="row g-2 mb-2 excursion-row">
                        <div class="col-md-8">
                            <select class="form-select" name="excursionIds">
                                <c:forEach var="e" items="${allExcursions}">
                                    <option value="${e.id}" ${e.id == scheduled.excursion.id ? 'selected' : ''}>${e.title} (${e.durationMinutes} мин, ${e.price} ₽)</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3"><input class="form-control" type="time" name="startTimes" value="${scheduled.startTime}" required/></div>
                        <div class="col-md-1"><button type="button" class="btn btn-outline-danger w-100" onclick="this.closest('.excursion-row').remove()">×</button></div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <button class="btn btn-success">Сохранить</button>
    <a class="btn btn-outline-secondary" href="<c:url value='/tours'/>">Отмена</a>
</form>

<template id="excursionRowTemplate">
    <div class="row g-2 mb-2 excursion-row">
        <div class="col-md-8">
            <select class="form-select" name="excursionIds">
                <c:forEach var="e" items="${allExcursions}">
                    <option value="${e.id}">${e.title} (${e.durationMinutes} мин, ${e.price} ₽)</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-3"><input class="form-control" type="time" name="startTimes" required/></div>
        <div class="col-md-1"><button type="button" class="btn btn-outline-danger w-100" onclick="this.closest('.excursion-row').remove()">×</button></div>
    </div>
</template>

<script>
    function addExcursionRow() {
        const template = document.getElementById('excursionRowTemplate');
        document.getElementById('excursionRows').appendChild(template.content.cloneNode(true));
    }
</script>
