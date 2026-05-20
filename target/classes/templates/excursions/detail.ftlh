<div class="card shadow-sm">
  <div class="card-body">
    <h1>${(excursion.title)!''}</h1>
    <p class="text-muted">⏱ ${(excursion.durationMinutes)!''} мин</p>
    <p>${(excursion.description)!''}</p>
    <div class="h4 price" data-rub="${((excursion.price)!0)?c}">${(excursion.price)!''} ₽</div>
    <a class="btn btn-outline-secondary mt-3" href="/excursions">К списку экскурсий</a>
  </div>
</div>
