<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm login-card">
            <div class="card-body">
                <h3 class="mb-3 text-center">Регистрация</h3>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">${errorMessage}</div>
                </c:if>
                <c:if test="${not empty errors}">
                    <div class="alert alert-danger mb-2">
                        <ul class="mb-0">
                            <c:forEach var="e" items="${errors}"><li>${e}</li></c:forEach>
                        </ul>
                    </div>
                </c:if>

                <form method="post" action="<c:url value='/auth/register'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <input class="form-control mb-2" name="username" placeholder="Username" value="${registerForm.username}" required/>
                    <input class="form-control mb-2" name="email" placeholder="Email" value="${registerForm.email}" required/>
                    <input class="form-control mb-2" type="password" name="password" placeholder="Password" required/>

                    <label class="mt-2">Role (temporary):</label>
                    <select class="form-select mb-3" name="role">
                        <option value="USER">USER</option>
                        <option value="MANAGER">MANAGER</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>

                    <button class="btn btn-primary w-100">Зарегистрироваться</button>
                </form>
            </div>
        </div>
    </div>
</div>
