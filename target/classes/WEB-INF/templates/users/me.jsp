<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <h2>Мой профиль</h2>
                <form method="post" action="<c:url value='/users/me'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">Username: <b>${user.username}</b></div>
                    <input class="form-control mb-2" name="email" value="${user.email}" required/>
                    <input class="form-control mb-2" type="password" name="password" placeholder="Новый пароль (опционально)"/>
                    <button class="btn btn-success">Сохранить</button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body">
                <h4>Покупки</h4>
                <div class="d-grid gap-2">
                    <a class="btn btn-primary" href="<c:url value='/orders/cart'/>">Корзина</a>
                    <a class="btn btn-outline-primary" href="<c:url value='/orders/history'/>">История заказов</a>
                </div>
            </div>
        </div>
    </div>
</div>
