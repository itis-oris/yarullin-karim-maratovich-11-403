<#assign canManage = canManageContent!false>
<h2>Отели</h2>
<#if canManage>
  <a href="/hotels/create" class="btn btn-primary mb-3">Создать отель</a>
</#if>

<table class="table table-striped align-middle">
  <thead><tr><th>ID</th><th>Название</th><th>Город</th><th>Звёзды</th><th>Эконом/ночь</th><th></th></tr></thead>
  <tbody>
  <#list hotels![] as h>
    <tr>
      <td>${h.id}</td>
      <td>${h.name!''}</td>
      <td>${h.city!''}</td>
      <td>${h.stars!''}</td>
      <td><span class="price" data-rub="${((h.economyPricePerNight)!0)?c}">${(h.economyPricePerNight)!0} ₽</span></td>
      <td class="text-end">
        <#if canManage>
          <a class="btn btn-sm btn-outline-secondary" href="/hotels/${h.id}/edit">Ред.</a>
          <form method="post" action="/hotels/${h.id}/delete" class="d-inline">
            <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
            <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
          </form>
        </#if>
      </td>
    </tr>
  <#else>
    <tr><td colspan="6" class="text-center text-muted">Отели не найдены</td></tr>
  </#list>
  </tbody>
</table>
<#include "/pagination.ftlh">
