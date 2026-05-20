<h2>Корзина</h2>
<#if !(orders?? && (orders?size > 0))>
  <div class="alert alert-info">Корзина пуста. <a href="/tours">Выберите тур</a>.</div>
<#else>
  <table class="table table-striped align-middle">
    <thead><tr><th>Тур</th><th>Параметры</th><th>Расчёт</th><th>Статус</th><th>Сумма</th><th></th></tr></thead>
    <tbody>
    <#list orders as order>
      <tr>
        <td><a href="/tours/${order.tour.id}">${order.tour.title!''}</a></td>
        <td class="small">
          ${order.bookingStartDate!''} — ${order.bookingEndDate!''}<br/>
          ${(order.nights)!1} ноч., ${(order.guests)!1} чел., ${order.roomLevel!'ECONOMY'}
        </td>
        <td class="small">
          Доп.: <span class="price" data-rub="${((order.tourExtraPrice)!0)?c}">${(order.tourExtraPrice)!0} ₽</span><br/>
          Отель: <span class="price" data-rub="${((order.hotelPrice)!0)?c}">${(order.hotelPrice)!0} ₽</span><br/>
          Экскурсии: <span class="price" data-rub="${((order.excursionsPrice)!0)?c}">${(order.excursionsPrice)!0} ₽</span>
        </td>
        <td>${order.status!''}</td>
        <td><span class="price" data-rub="${((order.totalPrice)!0)?c}">${order.totalPrice!''} ₽</span></td>
        <td class="text-end">
          <form method="post" action="/orders/${order.id}/remove" class="d-inline">
            <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
            <button class="btn btn-sm btn-outline-danger" type="submit">Удалить</button>
          </form>
        </td>
      </tr>
    </#list>
    </tbody>
  </table>
  <form method="post" action="/orders/checkout">
    <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
    <button class="btn btn-success" type="submit">Оформить заказ</button>
    <a class="btn btn-outline-secondary" href="/tours">Продолжить выбор</a>
  </form>
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
