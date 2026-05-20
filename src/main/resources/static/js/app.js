console.log('Tour Booking UI loaded');

window.currencyRatesPromise = null;

const FALLBACK_RUB_RATES = {RUB: 1, USD: 0.011, EUR: 0.010};

function normalizeRubRates(rates) {
    const normalized = {...FALLBACK_RUB_RATES, ...(rates || {})};
    ['USD', 'EUR'].forEach((currency) => {
        const rate = Number.parseFloat(normalized[currency]);
        normalized[currency] = Number.isFinite(rate) && rate >= 0.001 && rate <= 0.05
            ? rate
            : FALLBACK_RUB_RATES[currency];
    });
    normalized.RUB = 1;
    return normalized;
}

window.getCurrencyRates = function getCurrencyRates() {
    if (!window.currencyRatesPromise) {
        window.currencyRatesPromise = fetch('/api/v1/currency/rates?base=RUB', {
            headers: {'X-Requested-With': 'XMLHttpRequest'}
        })
            .then((response) => response.ok ? response.json() : FALLBACK_RUB_RATES)
            .then(normalizeRubRates)
            .catch(() => normalizeRubRates(FALLBACK_RUB_RATES));
    }
    return window.currencyRatesPromise;
};

window.updateCurrency = function updateCurrency() {
    const selector = document.getElementById('currencySelector');
    if (!selector) return;

    const currency = selector.value;
    const symbols = {RUB: '₽', USD: '$', EUR: '€'};
    window.getCurrencyRates().then((rates) => {
        document.querySelectorAll('.price[data-rub]').forEach((element) => {
            const rub = Number.parseFloat(String(element.dataset.rub || '0').replace(/[\s\u00A0]/g, '').replace(',', '.'));
            const prefix = element.dataset.prefix || (element.textContent.trim().startsWith('от') ? 'от ' : '');
            const converted = rub * (Number.parseFloat(rates[currency]) || 1);
            element.textContent = currency === 'RUB'
                ? `${prefix}${rub.toFixed(2)} ${symbols.RUB}`
                : `${prefix}${symbols[currency]}${converted.toFixed(2)}`;
        });
    });
};
