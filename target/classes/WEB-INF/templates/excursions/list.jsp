<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<h2>Экскурсии</h2>
<sec:authorize access="hasAnyRole('MANAGER','ADMIN')"><a href="<c:url value='/excursions/create'/>" class="btn btn-primary mb-3">Создать экскурсию</a></sec:authorize>
<table class="table table-striped"><tr><th>ID</th><th>Название</th><th>Цена</th><th></th></tr>
<c:forEach var="e" items="${excursions}"><tr><td>${e.id}</td><td>${e.title}</td><td>${e.price}</td><td>
<a class="btn btn-sm btn-outline-secondary" href="<c:url value='/excursions/${e.id}/edit'/>">Ред.</a>
<form method="post" action="<c:url value='/excursions/${e.id}/delete'/>" style="display:inline"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><button class="btn btn-sm btn-outline-danger">Удалить</button></form>
</td></tr></c:forEach></table>

<jsp:include page="/WEB-INF/templates/pagination.jsp" />
