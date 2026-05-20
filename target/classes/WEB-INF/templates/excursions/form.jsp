<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h2>${excursion.id == null ? 'Создание экскурсии' : 'Редактирование экскурсии'}</h2>
<c:choose>
<c:when test="${excursion.id == null}"><c:set var="actionUrl" value="/excursions/create"/></c:when>
<c:otherwise><c:set var="actionUrl" value="/excursions/${excursion.id}/edit"/></c:otherwise>
</c:choose>
<form method="post" action="<c:url value='${actionUrl}'/>">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<input class="form-control mb-2" name="title" value="${excursion.title}" placeholder="Название" required/>
<input class="form-control mb-2" name="description" value="${excursion.description}" placeholder="Описание"/>
<input class="form-control mb-2" type="number" name="durationMinutes" value="${excursion.durationMinutes}" placeholder="Длительность" required/>
<input class="form-control mb-2" type="number" step="0.01" name="price" value="${excursion.price}" placeholder="Цена" required/>
<button class="btn btn-success">Сохранить</button>
</form>
