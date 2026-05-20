<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h2>${hotel.id == null ? 'Создание отеля' : 'Редактирование отеля'}</h2>
<c:choose>
<c:when test="${hotel.id == null}"><c:set var="actionUrl" value="/hotels/create"/></c:when>
<c:otherwise><c:set var="actionUrl" value="/hotels/${hotel.id}/edit"/></c:otherwise>
</c:choose>
<form method="post" action="<c:url value='${actionUrl}'/>">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<input class="form-control mb-2" name="name" value="${hotel.name}" placeholder="Название" required/>
<input class="form-control mb-2" name="city" value="${hotel.city}" placeholder="Город"/>
<input class="form-control mb-2" type="number" name="stars" value="${hotel.stars}" placeholder="Звезды"/>
<input class="form-control mb-2" name="description" value="${hotel.description}" placeholder="Описание"/>
<button class="btn btn-success">Сохранить</button>
</form>
