<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
  <div class="container">
    <a class="navbar-brand fw-bold" href="/tours">Tour Booking</a>
    <div class="ms-auto d-flex gap-2 align-items-center flex-wrap justify-content-end">
      <a class="btn btn-sm btn-light" href="/tours">Туры</a>
      <a class="btn btn-sm btn-light" href="/excursions">Экскурсии</a>
      <a class="btn btn-sm btn-light" href="/hotels">Отели</a>

      <#if canManageContent!false>
        <a class="btn btn-sm btn-success" href="/tours/create">+ Тур</a>
        <a class="btn btn-sm btn-success" href="/excursions/create">+ Экскурсия</a>
        <a class="btn btn-sm btn-success" href="/hotels/create">+ Отель</a>
      </#if>

      <#if hasAdminAccess!false>
        <a class="btn btn-sm btn-outline-light" href="/users">Пользователи</a>
      </#if>

      <#if isAuthenticated!false>
        <a class="btn btn-sm btn-outline-light" href="/users/me">Профиль</a>
        <a class="btn btn-sm btn-outline-light" href="/orders/cart">Корзина</a>
        <form method="post" action="/auth/logout" class="d-inline">
          <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}" />
          <button class="btn btn-sm btn-warning" type="submit">Выйти</button>
        </form>
        <#else>
          <a class="btn btn-sm btn-outline-light" href="/auth/login">Войти</a>
      </#if>
    </div>
  </div>
</nav>