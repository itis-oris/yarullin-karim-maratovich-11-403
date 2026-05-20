<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="<c:url value='/tours'/>">Tour Booking</a>
        <div class="ms-auto d-flex gap-2">
            <a class="btn btn-sm btn-light" href="<c:url value='/tours'/>">Туры</a>
            <a class="btn btn-sm btn-light" href="<c:url value='/excursions'/>">Экскурсии</a>
            <a class="btn btn-sm btn-light" href="<c:url value='/hotels'/>">Отели</a>

            <sec:authorize access="isAnonymous()">
                <a class="btn btn-sm btn-outline-light" href="<c:url value='/auth/login'/>">Войти</a>
            </sec:authorize>

            <sec:authorize access="isAuthenticated()">
                <a class="btn btn-sm btn-outline-light" href="<c:url value='/users/me'/>">Профиль</a>
                <a class="btn btn-sm btn-outline-light" href="<c:url value='/orders/cart'/>">Корзина</a>
                <form method="post" action="<c:url value='/auth/logout'/>" class="d-inline">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-sm btn-warning" type="submit">Выйти</button>
                </form>
            </sec:authorize>
        </div>
    </div>
</nav>
