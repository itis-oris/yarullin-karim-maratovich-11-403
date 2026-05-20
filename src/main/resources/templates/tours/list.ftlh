<div class="d-flex justify-content-between align-items-center mb-4">
  <h1>Доступные туры</h1>
  <#if canManageContent!false>
    <a href="/tours/create" class="btn btn-success">➕ Новый тур</a>
  </#if>
</div>

<div class="card mb-4 shadow-sm">
  <div class="card-body">
    <form id="filterForm" class="row g-3">
      <div class="col-md-4"><label class="form-label">Дата начала от:</label><input type="date" class="form-control"
          id="startDate" name="startDate"></div>
      <div class="col-md-4"><label class="form-label">Дата окончания до:</label><input type="date" class="form-control"
          id="endDate" name="endDate"></div>
      <div class="col-md-4 d-flex align-items-end"><button type="submit" class="btn btn-primary w-100">🔍
          Фильтровать</button></div>
    </form>
  </div>
</div>

<div id="toursList" class="row g-4">
  <#list (tours![]) as tour>
    <div class="col-md-6 col-lg-4">
      <div class="card h-100 shadow-sm hover-shadow">
        <div class="card-body">
          <h5 class="card-title">${tour.title}</h5>
          <p class="card-text text-muted small">📅 ${tour.startDate} - ${tour.endDate}</p>
          <p class="card-text">${tour.description}</p>
          <div class="d-flex justify-content-between align-items-center mt-3">
            <span class="badge bg-primary">${(tour.excursions?size)!0} экскурсий</span>
            <#assign displayPrice=(tour.totalPrice)!((tour.basePrice)!0)>
              <span class="fw-bold price" data-rub="${displayPrice?c}">от ${displayPrice} ₽</span>
          </div>
        </div>
        <div class="card-footer bg-white border-top-0">
          <div class="d-grid gap-2">
            <a href="/tours/${tour.id}" class="btn btn-outline-primary">Подробнее</a>
            <#if request?? && (request.userPrincipal)??>
              <form method="post" action="/orders/add-tour">
                <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}" />
                <input type="hidden" name="tourId" value="${tour.id}" />
                <input type="hidden" name="bookingStartDate" value="${tour.startDate}" />
                <input type="hidden" name="bookingEndDate" value="${tour.endDate}" />
                <input type="hidden" name="guests" value="1" />
                <input type="hidden" name="roomLevel" value="ECONOMY" />
                <button class="btn btn-primary w-100" type="submit">🛒 Эконом на 1 чел.</button>
              </form>
            </#if>
            <#if canManageContent!false>
              <div class="btn-group w-100 mt-2">
                <a href="/tours/${tour.id}/edit" class="btn btn-sm btn-outline-secondary">✏️</a>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteTour(${tour.id})">🗑️</button>
              </div>
            </#if>
          </div>
        </div>
      </div>
    </div>
  </#list>
</div>

<#if !(tours?? && (tours?size> 0))>
  <div class="text-center py-5">
    <h4 class="text-muted">Туры не найдены</h4>
    <p class="text-muted">Попробуйте изменить параметры фильтрации</p>
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

<script>
  document.getElementById('filterForm').addEventListener('submit', function (e) { e.preventDefault(); filterTours(); });
  function filterTours() {
    const startDate = document.getElementById('startDate').value; const endDate = document.getElementById('endDate').value;
    fetch('/tours/filter?startDate=' + encodeURIComponent(startDate) + '&endDate=' + encodeURIComponent(endDate), { headers: { 'X-Requested-With': 'XMLHttpRequest', 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content } })
      .then(response => response.text()).then(html => { document.getElementById('toursList').innerHTML = html; })
      .catch(error => { showNotification('Ошибка при фильтрации', 'error'); console.error('Filter error:', error); });
  }
  function deleteTour(tourId) {
    if (!confirm('Вы уверены, что хотите отменить этот тур?')) return;
    fetch('/tours/' + tourId + '/delete', { method: 'POST', headers: { 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content } })
      .then(response => { if (response.ok) { location.reload(); } else { showNotification('Ошибка при удалении', 'error'); } });
  }
  function showNotification(message, type) {
    const container = document.getElementById('notification-area'); const alert = document.createElement('div');
    alert.className = 'alert alert-' + type + ' alert-dismissible fade show'; alert.innerHTML = message + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    container.appendChild(alert); setTimeout(() => alert.remove(), 5000);
  } 
</script>
<#include "/pagination.ftlh">