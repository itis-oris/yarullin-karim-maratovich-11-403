package com.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CurrencyConversionService {

  private static final String DEFAULT_CURRENCY = "RUB";
  private static final BigDecimal FALLBACK_RUB_TO_USD = new BigDecimal("0.011");
  private static final BigDecimal FALLBACK_RUB_TO_EUR = new BigDecimal("0.010");

  @Value("${app.currency.api-url}")
  private String exchangeRateApiUrl;

  private final OkHttpClient httpClient =
      new OkHttpClient.Builder()
          .connectTimeout(15, TimeUnit.SECONDS)
          .readTimeout(15, TimeUnit.SECONDS)
          .build();

  /** Получает курс валют с кэшированием в Redis на 1 час. */
  @Cacheable(
      value = "exchangeRates",
      key = "#fromCurrency + '_' + #toCurrency",
      unless = "#result == null")
  public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
    String from = normalizeCurrency(fromCurrency);
    String to = normalizeCurrency(toCurrency);
    if (from.equals(to)) {
      return BigDecimal.ONE;
    }

    try {
      log.debug("Fetching exchange rate: {} -> {}", from, to);

      // Используем CBR XML Daily API
      Request request = new Request.Builder().url(exchangeRateApiUrl).get().build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (!response.isSuccessful() || response.body() == null) {
          log.warn("Currency API returned error code: {}", response.code());
          return fallbackRate(from, to);
        }

        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);

        // CBR XML Daily API возвращает: Valute -> {USD: {CharCode, Value, Nominal, ...}}
        JSONObject valutes = json.getJSONObject("Valute");

        // Для перевода из одной валюты в другую через RUB
        // Получаем курсы валют относительно RUB
        BigDecimal fromRateToRub = getCurrencyToRub(valutes, from);
        BigDecimal toRateToRub = getCurrencyToRub(valutes, to);

        if (fromRateToRub != null
            && toRateToRub != null
            && fromRateToRub.compareTo(BigDecimal.ZERO) > 0) {
          // Конвертируем: from -> RUB -> to
          BigDecimal rate = fromRateToRub.divide(toRateToRub, 8, RoundingMode.HALF_UP);
          log.debug("Exchange rate {}->{}: {}", from, to, rate);
          return rate;
        }
      }

    } catch (Exception ex) {
      log.error("Error fetching exchange rate from external API: ", ex);
      return fallbackRate(from, to);
    }

    return fallbackRate(from, to);
  }

  /**
   * Получает курс валюты к рублю из JSON CBR. Возвращает стоимость 1 единицы валюты в рублях.
   * Формат API: {CharCode: "USD", Value: 74.2963, Nominal: 1}
   */
  private BigDecimal getCurrencyToRub(JSONObject valutes, String currencyCode) {
    if ("RUB".equals(currencyCode)) {
      return BigDecimal.ONE;
    }

    try {
      // У CBR в JSON ключи объектов — это коды валют (напр. "USD"),
      // можно обращаться напрямую вместо итератора
      if (valutes.has(currencyCode.toUpperCase())) {
        JSONObject currency = valutes.getJSONObject(currencyCode.toUpperCase());

        // Используем get().toString(), чтобы не зависеть от того,
        // пришло число или строка в JSON
        String valueStr = currency.get("Value").toString().replace(",", ".");
        String nominalStr = currency.get("Nominal").toString().replace(",", ".");

        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal nominal = new BigDecimal(nominalStr);

        return value.divide(nominal, 8, RoundingMode.HALF_UP);
      }
    } catch (Exception e) {
      log.warn("Error parsing currency {} from CBR response: {}", currencyCode, e.getMessage());
    }

    return null;
  }

  /**
   * Возвращает коэффициенты для пересчёта из рублей в основные валюты. Для USD/EUR используем
   * обратный курс к RUB как запасной вариант: это защищает UI от некорректного курса вроде 0.1,
   * из-за которого сумма визуально делилась примерно на 10.
   */
  public Map<String, BigDecimal> getRubRates() {
    return Map.of(
        "RUB", BigDecimal.ONE,
        "USD", getRubToCurrencyRate("USD", FALLBACK_RUB_TO_USD),
        "EUR", getRubToCurrencyRate("EUR", FALLBACK_RUB_TO_EUR));
  }

  /** Конвертирует сумму из RUB в целевую валюту. */
  public BigDecimal convertPrice(BigDecimal amountRub, String toCurrency) {
    if (amountRub == null) return BigDecimal.ZERO;

    BigDecimal rate =
        getRubToCurrencyRate(
            toCurrency, fallbackRate(DEFAULT_CURRENCY, normalizeCurrency(toCurrency)));
    return amountRub.multiply(rate).setScale(2, RoundingMode.HALF_UP);
  }

  /** Форматирует цену для отображения с валютой. */
  public String formatPrice(BigDecimal amountRub, String currency) {
    if ("RUB".equalsIgnoreCase(currency)) {
      return String.format("%,.2f ₽", amountRub);
    }
    BigDecimal converted = convertPrice(amountRub, currency);
    String symbol =
        switch (currency.toUpperCase()) {
          case "USD" -> "$";
          case "EUR" -> "€";
          case "GBP" -> "£";
          default -> currency + " ";
        };
    return String.format("%s%,.2f", symbol, converted);
  }

  private BigDecimal getRubToCurrencyRate(String toCurrency, BigDecimal fallback) {
    String to = normalizeCurrency(toCurrency);
    if (DEFAULT_CURRENCY.equals(to)) {
      return BigDecimal.ONE;
    }

    // Запрашиваем курс RUB -> USD напрямую
    BigDecimal rate = getExchangeRate(DEFAULT_CURRENCY, to);

    // Проверяем, что курс адекватный (не 0 и не ошибка)
    if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0 && isReasonableRubRate(rate)) {
      return rate;
    }

    return fallback;
  }

  private boolean isReasonableRubRate(BigDecimal rate) {
    return rate != null
        && rate.compareTo(new BigDecimal("0.001")) >= 0
        && rate.compareTo(new BigDecimal("0.05")) <= 0;
  }

  private String normalizeCurrency(String currency) {
    return currency == null || currency.isBlank()
        ? DEFAULT_CURRENCY
        : currency.trim().toUpperCase();
  }

  private BigDecimal fallbackRate(String fromCurrency, String toCurrency) {
    String from = normalizeCurrency(fromCurrency);
    String to = normalizeCurrency(toCurrency);
    if (from.equals(to)) {
      return BigDecimal.ONE;
    }
    if (DEFAULT_CURRENCY.equals(from) && "USD".equals(to)) {
      return FALLBACK_RUB_TO_USD;
    }
    if (DEFAULT_CURRENCY.equals(from) && "EUR".equals(to)) {
      return FALLBACK_RUB_TO_EUR;
    }
    if ("USD".equals(from) && DEFAULT_CURRENCY.equals(to)) {
      return BigDecimal.ONE.divide(FALLBACK_RUB_TO_USD, 8, RoundingMode.HALF_UP);
    }
    if ("EUR".equals(from) && DEFAULT_CURRENCY.equals(to)) {
      return BigDecimal.ONE.divide(FALLBACK_RUB_TO_EUR, 8, RoundingMode.HALF_UP);
    }
    return BigDecimal.ONE;
  }
}
