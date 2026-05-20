<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}" default="Tour Booking"/></title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS с объяснением классов -->
    <link href="<c:url value='/css/main.css'/>" rel="stylesheet">

    <!-- CSRF Token для AJAX -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">

<%-- Header (модульная часть) --%>
<jsp:include page="/WEB-INF/templates/header.jsp"/>

<%-- Main Content --%>
<main class="flex-grow-1 container py-4">
    <%-- Сообщения об успехе --%>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <%-- Сообщения об ошибках --%>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <%-- Ошибки валидации --%>
    <c:if test="${not empty errors}">
        <div class="alert alert-danger">
            <ul class="mb-0">
                <c:forEach var="error" items="${errors}">
                    <li>${error}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <%-- Контент страницы --%>
    <c:set var="resolvedPageContent" value="${fn:startsWith(pageContent, '/') ? pageContent : '/WEB-INF/templates/'.concat(pageContent)}"/>
    <jsp:include page="${resolvedPageContent}"/>
</main>

<%-- Footer (модульная часть) --%>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>

<%-- Контейнер для уведомлений AJAX --%>
<div id="notification-area" class="position-fixed bottom-0 end-0 p-3" style="z-index: 1100;"></div>

<!-- Bootstrap JS + Custom JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/js/app.js'/>"></script>
<script src="<c:url value='/js/ajax-utils.js'/>"></script>

</body>
</html>
