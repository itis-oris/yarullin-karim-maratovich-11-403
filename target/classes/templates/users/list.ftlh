<h2>Пользователи</h2>
<table class="table table-striped align-middle">
  <thead><tr><th>ID</th><th>Username</th><th>Email</th><th>Role</th><th></th></tr></thead>
  <tbody>
  <#list users![] as u>
    <tr>
      <td>${u.id}</td>
      <td>${u.username!''}</td>
      <td>${u.email!''}</td>
      <td>${u.role!''}</td>
      <td>
        <form method="post" action="/users/${u.id}/role" class="d-flex gap-2">
          <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
          <select name="role" class="form-select form-select-sm w-auto">
            <option value="USER" <#if (u.role?string) == 'USER'>selected</#if>>USER</option>
            <option value="MANAGER" <#if (u.role?string) == 'MANAGER'>selected</#if>>MANAGER</option>
            <option value="ADMIN" <#if (u.role?string) == 'ADMIN'>selected</#if>>ADMIN</option>
          </select>
          <button class="btn btn-sm btn-primary" type="submit">Сменить</button>
        </form>
      </td>
    </tr>
  <#else>
    <tr><td colspan="5" class="text-center text-muted">Пользователи не найдены</td></tr>
  </#list>
  </tbody>
</table>

<#include "/pagination.ftlh">
