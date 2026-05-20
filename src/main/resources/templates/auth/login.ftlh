<div class="row justify-content-center">
  <div class="col-md-5">
    <div class="card shadow-sm login-card">
      <div class="card-body">
        <h3 class="mb-3 text-center">Вход</h3>
        <form method="post" action="/auth/login">
          <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}" />
          <input class="form-control mb-2" name="username" placeholder="Логин" required />
          <input class="form-control mb-3" type="password" name="password" placeholder="Пароль" required />
          <div class="auth-actions">
            <button class="btn btn-primary w-100">Войти</button>
            <a class="btn btn-outline-secondary w-100 mt-2" href="/auth/register">Зарегистрироваться</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>