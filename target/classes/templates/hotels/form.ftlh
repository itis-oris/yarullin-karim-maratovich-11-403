<#assign isEdit = (hotel.id)??>
<#assign actionUrl = isEdit?then('/hotels/' + hotel.id + '/edit', '/hotels/create')>
<h2>${isEdit?then('Редактирование отеля', 'Создание отеля')}</h2>

<form method="post" action="${actionUrl}">
  <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
  <input class="form-control mb-2" name="name" value="${(hotel.name)!''}" placeholder="Название" required/>
  <input class="form-control mb-2" name="city" value="${(hotel.city)!''}" placeholder="Город"/>
  <input class="form-control mb-2" type="number" name="stars" value="${(hotel.stars)!''}" placeholder="Звезды"/>
  <input class="form-control mb-2" name="description" value="${(hotel.description)!''}" placeholder="Описание"/>

  <div class="card shadow-sm mb-3">
    <div class="card-body">
      <h5 class="card-title">Стоимость за ночь</h5>
      <div class="row g-2">
        <div class="col-md-4">
          <label class="form-label">Эконом</label>
          <input class="form-control" type="number" step="0.01" min="0" name="economyPricePerNight" value="${(hotel.economyPricePerNight)!''}" placeholder="₽ за ночь"/>
        </div>
        <div class="col-md-4">
          <label class="form-label">Стандарт</label>
          <input class="form-control" type="number" step="0.01" min="0" name="standardPricePerNight" value="${(hotel.standardPricePerNight)!''}" placeholder="₽ за ночь"/>
        </div>
        <div class="col-md-4">
          <label class="form-label">Люкс</label>
          <input class="form-control" type="number" step="0.01" min="0" name="luxuryPricePerNight" value="${(hotel.luxuryPricePerNight)!''}" placeholder="₽ за ночь"/>
        </div>
      </div>
    </div>
  </div>

  <button class="btn btn-success" type="submit">Сохранить</button>
  <a class="btn btn-outline-secondary" href="/hotels">Отмена</a>
</form>
