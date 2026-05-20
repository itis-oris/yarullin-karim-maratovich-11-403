<#assign isEdit = (excursion.id)??>
<#assign actionUrl = isEdit?then('/excursions/' + excursion.id + '/edit', '/excursions/create')>
<h2>${isEdit?then('Редактирование экскурсии', 'Создание экскурсии')}</h2>

<form method="post" action="${actionUrl}">
  <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}"/>
  <input class="form-control mb-2" name="title" value="${(excursion.title)!''}" placeholder="Название" required/>
  <input class="form-control mb-2" name="description" value="${(excursion.description)!''}" placeholder="Описание"/>
  <input class="form-control mb-2" type="number" name="durationMinutes" value="${(excursion.durationMinutes)!''}" placeholder="Длительность" required/>
  <input class="form-control mb-2" type="number" step="0.01" name="price" value="${(excursion.price)!''}" placeholder="Цена" required/>
  <button class="btn btn-success" type="submit">Сохранить</button>
  <a class="btn btn-outline-secondary" href="/excursions">Отмена</a>
</form>
