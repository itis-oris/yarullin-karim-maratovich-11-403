<h2>История заказов</h2>
<#if !(orders?? && (orders?size> 0))>
  <div class="alert alert-info">История пока пуста.</div>
  <#else>
    <table class="table table-striped align-middle">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Тур</th>
          <th>Параметры</th>
          <th>Статус</th>
          <th>Сумма</th>
        </tr>
      </thead>
      <tbody>
        <#list orders as order>
          <tr>
            <td>${order.createdAt!''}</td>
            <td><a href="/tours/${order.tour.id}">${order.tour.title!''}</a></td>
            <td class="small">${order.bookingStartDate!''} — ${order.bookingEndDate!''}, ${(order.guests)!1} чел.,
              ${order.roomLevel!'ECONOMY'}</td>
            <td>${order.status!''}</td>
            <td><span class="price" data-rub="${((order.totalPrice)!0)?c}">${order.totalPrice!''} ₽</span></td>
          </tr>
        </#list>
      </tbody>
    </table>
</#if>

<div class="mt-4 text-end">
  <label class="me-2">Показать цены в:</label>
  <select id="currencySelector" class="form-select d-inline-block w-auto" onchange="updateCurrency()">
    <option value="RUB">RUB ₽</option>
    <option value="USD">USD $</option>
    <option value="EUR">EUR €</option>
  </select>
</div>

<#include "/pagination.ftlh">