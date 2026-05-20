<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="table table-striped">
    <tr><th>ID</th><th>Username</th><th>Email</th><th>Role</th><th></th></tr>
    <c:forEach var="u" items="${users}">
        <tr>
            <td>${u.id}</td><td>${u.username}</td><td>${u.email}</td><td>${u.role}</td>
            <td>
                <form method="post" action="<c:url value='/users/${u.id}/role'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <select name="role" class="form-select form-select-sm d-inline w-auto">
                        <option value="USER" <c:if test="${u.role.name() == 'USER'}">selected</c:if>>USER</option>
                        <option value="MANAGER" <c:if test="${u.role.name() == 'MANAGER'}">selected</c:if>>MANAGER</option>
                        <option value="ADMIN" <c:if test="${u.role.name() == 'ADMIN'}">selected</c:if>>ADMIN</option>
                    </select>
                    <button class="btn btn-sm btn-primary">Сменить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<jsp:include page="/WEB-INF/templates/pagination.jsp" />
