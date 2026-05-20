<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty page and page.totalPages > 1}">
    <nav aria-label="Page navigation" class="mt-3">
        <ul class="pagination justify-content-center">
            <li class="page-item ${page.first ? 'disabled' : ''}">
                <a class="page-link" href="?page=${page.number - 1}&size=${page.size}">Назад</a>
            </li>

            <c:forEach var="i" begin="0" end="${page.totalPages - 1}">
                <li class="page-item ${i == page.number ? 'active' : ''}">
                    <a class="page-link" href="?page=${i}&size=${page.size}">${i + 1}</a>
                </li>
            </c:forEach>

            <li class="page-item ${page.last ? 'disabled' : ''}">
                <a class="page-link" href="?page=${page.number + 1}&size=${page.size}">Вперёд</a>
            </li>
        </ul>
    </nav>
</c:if>
