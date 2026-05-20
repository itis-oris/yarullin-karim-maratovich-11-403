<#assign isEdit = (tour.id)??>
<#assign actionUrl = isEdit?then('/tours/' + tour.id + '/edit', '/tours/create')>
<h2>${isEdit?then('Редактирование тура', 'Создание тура')}</h2>

<#if errorMessage??><div class="alert alert-danger">${errorMessage}</div></#if>
<#if errors?? && (errors?size > 0)>
  <div class="alert alert-danger"><ul class="mb-0"><#list errors as e><li>${e}</li></#list></ul></div>
</#if>

<form method="post" action="${actionUrl}">
  <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>

  <div class="card shadow-sm mb-3">
    <div class="card-body">
      <h5 class="card-title">Основная информация</h5>
      <input class="form-control mb-2" name="title" value="${(tour.title)!''}" placeholder="Название" required/>
      <textarea class="form-control mb-2" name="description" placeholder="Описание" rows="3">${(tour.description)!''}</textarea>
      <div class="row g-2">
        <div class="col-md-4"><input id="tourStartDateInput" class="form-control" type="date" name="startDate" value="${(tour.startDate)!''}" oninput="updateTourPriceSummary()" required/></div>
        <div class="col-md-4"><input id="tourEndDateInput" class="form-control" type="date" name="endDate" value="${(tour.endDate)!''}" oninput="updateTourPriceSummary()" required/></div>
        <div class="col-md-4">
          <input id="basePriceInput" class="form-control" type="number" step="0.01" min="0" name="basePrice" value="${((tour.basePrice)!0)?c}" placeholder="Доп. стоимость тура" oninput="updateTourPriceSummary()"/>
          <div class="form-text">Это отдельная доп. стоимость тура без экскурсий.</div>
        </div>
      </div>
    </div>
  </div>

  <div class="card shadow-sm mb-3">
    <div class="card-body">
      <h5 class="card-title">Отель</h5>
      <select id="hotelSelect" class="form-select" name="hotelId" onchange="updateTourPriceSummary()">
        <option value="">Без отеля</option>
        <#list hotels![] as hotel>
          <option value="${hotel.id}" data-economy-price="${((hotel.economyPricePerNight)!0)?c}" <#if (tour.hotelId)?? && hotel.id == tour.hotelId>selected</#if>>${hotel.name!''} — ${hotel.city!''}, ${hotel.stars!''}★ (эконом: ${(hotel.economyPricePerNight)!0} ₽/ночь)</option>
        </#list>
      </select>
    </div>
  </div>

  <div class="card shadow-sm mb-3">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="card-title mb-0">Экскурсии по порядку</h5>
        <button type="button" class="btn btn-sm btn-outline-primary" onclick="addExcursionRow()">Добавить экскурсию</button>
      </div>
      <p class="text-muted small">Порядок строк задаёт порядок экскурсий в туре. Время окончания считается автоматически по длительности экскурсии.</p>
      <div id="excursionRows">
        <#list (tour.scheduledExcursions)![] as scheduled>
          <div class="row g-2 mb-2 excursion-row">
            <div class="col-md-8">
              <select class="form-select excursion-select" name="excursionIds" onchange="updateTourPriceSummary()">
                <#list allExcursions![] as e>
                  <option value="${e.id}" data-price="${((e.price)!0)?c}" <#if (scheduled.excursion.id)?? && e.id == scheduled.excursion.id>selected</#if>>${e.title!''} (${e.durationMinutes!''} мин, ${e.price!''} ₽)</option>
                </#list>
              </select>
            </div>
            <div class="col-md-3"><input class="form-control" type="time" name="startTimes" value="${scheduled.startTime!''}" required/></div>
            <div class="col-md-1"><button type="button" class="btn btn-outline-danger w-100" onclick="removeExcursionRow(this)">×</button></div>
          </div>
        </#list>
      </div>
    </div>
  </div>

  <div class="card shadow-sm mb-3">
    <div class="card-body">
      <h5 class="card-title">Расчёт стоимости</h5>
      <div class="d-flex justify-content-between"><span>Доп. стоимость тура:</span><strong><span id="basePriceSummary">0.00</span> ₽</strong></div>
      <div class="d-flex justify-content-between"><span>Сумма экскурсий за 1 человека:</span><strong><span id="excursionsPriceSummary">0.00</span> ₽</strong></div>
      <div class="d-flex justify-content-between"><span>Минимум отеля (эконом, 1 человек):</span><strong><span id="hotelPriceSummary">0.00</span> ₽</strong></div>
      <hr/>
      <div class="d-flex justify-content-between fs-5"><span>Минимум итого:</span><strong><span id="totalPriceSummary">0.00</span> ₽</strong></div>
    </div>
  </div>

  <button class="btn btn-success" type="submit">Сохранить</button>
  <a class="btn btn-outline-secondary" href="/tours">Отмена</a>
</form>

<template id="excursionRowTemplate">
  <div class="row g-2 mb-2 excursion-row">
    <div class="col-md-8">
      <select class="form-select excursion-select" name="excursionIds" onchange="updateTourPriceSummary()">
        <#list allExcursions![] as e>
          <option value="${e.id}" data-price="${((e.price)!0)?c}">${e.title!''} (${e.durationMinutes!''} мин, ${e.price!''} ₽)</option>
        </#list>
      </select>
    </div>
    <div class="col-md-3"><input class="form-control" type="time" name="startTimes" required/></div>
    <div class="col-md-1"><button type="button" class="btn btn-outline-danger w-100" onclick="removeExcursionRow(this)">×</button></div>
  </div>
</template>

<script>
function parseMoney(value) {
  const normalized = String(value || '0').replace(',', '.');
  const parsed = Number.parseFloat(normalized);
  return Number.isFinite(parsed) ? parsed : 0;
}

function formatMoney(value) {
  return value.toFixed(2);
}

function selectedExcursionsTotal() {
  return Array.from(document.querySelectorAll('.excursion-select'))
    .reduce((sum, select) => sum + parseMoney(select.selectedOptions[0]?.dataset.price), 0);
}

function calculateNights() {
  const start = document.getElementById('tourStartDateInput')?.value;
  const end = document.getElementById('tourEndDateInput')?.value;
  if (!start || !end) return 1;
  const diffMs = new Date(end).getTime() - new Date(start).getTime();
  const nights = Math.ceil(diffMs / 86400000);
  return Number.isFinite(nights) && nights > 0 ? nights : 1;
}

function selectedHotelMinimumTotal() {
  const hotelSelect = document.getElementById('hotelSelect');
  const economyPrice = parseMoney(hotelSelect?.selectedOptions[0]?.dataset.economyPrice);
  return economyPrice * calculateNights();
}

function updateTourPriceSummary() {
  const basePrice = parseMoney(document.getElementById('basePriceInput')?.value);
  const excursionsPrice = selectedExcursionsTotal();
  const hotelPrice = selectedHotelMinimumTotal();
  document.getElementById('basePriceSummary').textContent = formatMoney(basePrice);
  document.getElementById('excursionsPriceSummary').textContent = formatMoney(excursionsPrice);
  document.getElementById('hotelPriceSummary').textContent = formatMoney(hotelPrice);
  document.getElementById('totalPriceSummary').textContent = formatMoney(basePrice + excursionsPrice + hotelPrice);
}

function addExcursionRow() {
  const template = document.getElementById('excursionRowTemplate');
  document.getElementById('excursionRows').appendChild(template.content.cloneNode(true));
  updateTourPriceSummary();
}

function removeExcursionRow(button) {
  button.closest('.excursion-row').remove();
  updateTourPriceSummary();
}

document.addEventListener('DOMContentLoaded', updateTourPriceSummary);
</script>
