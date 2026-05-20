<#assign isAuthenticated = request?? && (request.userPrincipal)??>
<#assign displayPrice = (tour.totalPrice)!((tour.basePrice)!0)>
<#assign roomEconomy = (tour.hotelEconomyPricePerNight)!0>
<#assign roomStandard = (tour.hotelStandardPricePerNight)!roomEconomy>
<#assign roomLuxury = (tour.hotelLuxuryPricePerNight)!roomStandard>
<div class="card shadow-sm mb-4">
  <div class="card-body">
    <div class="d-flex justify-content-between align-items-start gap-3">
      <div>
        <h1 class="mb-2">${(tour.title)!''}</h1>
        <p class="text-muted mb-2">📅 ${(tour.startDate)!''} — ${(tour.endDate)!''}</p>
        <p>${(tour.description)!''}</p>
      </div>
      <div class="text-end text-nowrap">
        <div class="small text-muted">Минимум за 1 человека, эконом</div>
        <div class="h4 price" data-rub="${displayPrice?c}">${displayPrice} ₽</div>
      </div>
    </div>

    <hr/>
    <h5>Стоимость</h5>
    <div class="row g-2">
      <div class="col-md-4">
        <div class="border rounded p-2 h-100">
          <div class="small text-muted">Доп. стоимость тура</div>
          <strong class="price" data-rub="${((tour.basePrice)!0)?c}">${(tour.basePrice)!0} ₽</strong>
        </div>
      </div>
      <div class="col-md-4">
        <div class="border rounded p-2 h-100">
          <div class="small text-muted">Экскурсии за 1 человека</div>
          <strong class="price" data-rub="${((tour.excursionTotalPrice)!0)?c}">${(tour.excursionTotalPrice)!0} ₽</strong>
        </div>
      </div>
      <div class="col-md-4">
        <div class="border rounded p-2 h-100">
          <div class="small text-muted">Отель, эконом за даты тура</div>
          <strong class="price" data-rub="${((tour.minimumHotelPrice)!0)?c}">${(tour.minimumHotelPrice)!0} ₽</strong>
        </div>
      </div>
    </div>

    <#if (tour.hotelName)?has_content>
      <hr/>
      <h5>Отель</h5>
      <p class="mb-2">🏨 ${tour.hotelName} — ${(tour.hotelCity)!''}, ${(tour.hotelStars)!''}★</p>
      <div class="small text-muted">
        Эконом: <span class="price" data-rub="${roomEconomy?c}">${roomEconomy} ₽</span>/ночь,
        стандарт: <span class="price" data-rub="${roomStandard?c}">${roomStandard} ₽</span>/ночь,
        люкс: <span class="price" data-rub="${roomLuxury?c}">${roomLuxury} ₽</span>/ночь
      </div>
    </#if>
  </div>
</div>

<div class="card shadow-sm mb-4">
  <div class="card-body">
    <h4>Экскурсии в туре</h4>
    <#if !(tour.scheduledExcursions?? && (tour.scheduledExcursions?size > 0))>
      <p class="text-muted mb-0">Экскурсии ещё не добавлены.</p>
    <#else>
      <ol class="list-group list-group-numbered">
        <#list tour.scheduledExcursions as item>
          <li class="list-group-item d-flex justify-content-between align-items-start">
            <div>
              <a href="/excursions/${item.excursion.id}">${item.excursion.title!''}</a>
              <div class="small text-muted">${item.startTime!''}–${item.endTime!''}, ${item.excursion.durationMinutes!''} мин</div>
              <div>${item.excursion.description!''}</div>
            </div>
            <span class="badge bg-primary rounded-pill price" data-rub="${((item.excursion.price)!0)?c}">${item.excursion.price!''} ₽</span>
          </li>
        </#list>
      </ol>
    </#if>
  </div>
</div>

<div class="d-flex gap-2 mb-4">
  <a href="/tours" class="btn btn-outline-secondary">Назад к турам</a>
</div>

<#if isAuthenticated>
  <div class="card shadow-sm mb-4">
    <div class="card-body">
      <h4>Бронирование</h4>
      <p class="text-muted small">Итог будет рассчитан по выбранным датам, количеству человек и уровню комнаты.</p>
      <form method="post" action="/orders/add-tour" class="row g-3">
        <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
        <input type="hidden" name="tourId" value="${tour.id}"/>
        <div class="col-md-3">
          <label class="form-label">Дата от</label>
          <input class="form-control" type="date" name="bookingStartDate" value="${(tour.startDate)!''}" min="${(tour.startDate)!''}" max="${(tour.endDate)!''}" required/>
        </div>
        <div class="col-md-3">
          <label class="form-label">Дата до</label>
          <input class="form-control" type="date" name="bookingEndDate" value="${(tour.endDate)!''}" min="${(tour.startDate)!''}" max="${(tour.endDate)!''}" required/>
        </div>
        <div class="col-md-2">
          <label class="form-label">Человек</label>
          <input class="form-control" type="number" name="guests" value="1" min="1" required/>
        </div>
        <div class="col-md-2">
          <label class="form-label">Комната</label>
          <select class="form-select" name="roomLevel">
            <option value="ECONOMY">Эконом</option>
            <option value="STANDARD">Стандарт</option>
            <option value="LUXURY">Люкс</option>
          </select>
        </div>
        <div class="col-md-2 d-flex align-items-end">
          <button class="btn btn-primary w-100" type="submit">В корзину</button>
        </div>
      </form>
    </div>
  </div>
</#if>

<div class="mt-4 text-end">
  <label class="me-2">Показать цены в:</label>
  <select id="currencySelector" class="form-select d-inline-block w-auto" onchange="updateCurrency()">
    <option value="RUB">RUB ₽</option>
    <option value="USD">USD $</option>
    <option value="EUR">EUR €</option>
  </select>
</div>
