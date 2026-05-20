<#assign canManage=canManageContent!false>
  <h2>Экскурсии</h2>
  <#if canManage>
    <a href="/excursions/create" class="btn btn-primary mb-3">Создать экскурсию</a>
  </#if>

  <table class="table table-striped align-middle">
    <thead>
      <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Цена</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <#list excursions![] as e>
        <tr>
          <td>${e.id}</td>
          <td><a href="/excursions/${e.id}">${e.title!''}</a></td>
          <td>${e.price!''}</td>
          <td class="text-end">
            <#if canManage>
              <a class="btn btn-sm btn-outline-secondary" href="/excursions/${e.id}/edit">Ред.</a>
              <form method="post" action="/excursions/${e.id}/delete" class="d-inline">
                <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}" />
                <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
              </form>
            </#if>
          </td>
        </tr>
        <#else>
          <tr>
            <td colspan="4" class="text-center text-muted">Экскурсии не найдены</td>
          </tr>
      </#list>
    </tbody>
  </table>
  <#include "/pagination.ftlh">