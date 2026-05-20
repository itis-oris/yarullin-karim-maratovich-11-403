<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5"><div class="card shadow-sm login-card"><div class="card-body">
    <h3 class="mb-3 text-center">Регистрация</h3>
    <#if errorMessage??><div class="alert alert-danger">${errorMessage}</div></#if>
    <#if errors?? && (errors?size>0)><div class="alert alert-danger mb-2"><ul class="mb-0"><#list errors as e><li>${e}</li></#list></ul></div></#if>
    <form method="post" action="/auth/register">
      <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
      <input class="form-control mb-2" name="username" placeholder="Username" value="${(registerForm.username)!''}" required/>
      <input class="form-control mb-2" name="email" placeholder="Email" value="${(registerForm.email)!''}" required/>
      <input class="form-control mb-2" type="password" name="password" placeholder="Password" required/>
      <button class="btn btn-primary w-100" type="submit">Зарегистрироваться</button>
    </form>
  </div></div></div>
</div>
