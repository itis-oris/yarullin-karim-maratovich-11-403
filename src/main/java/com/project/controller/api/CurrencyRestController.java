package com.project.controller.api;

import com.project.service.CurrencyConversionService;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyRestController {

  private final CurrencyConversionService currencyConversionService;

  @GetMapping("/rates")
  public ResponseEntity<Map<String, BigDecimal>> rates(
      @RequestParam(defaultValue = "RUB") String base) {
    String normalizedBase = base.toUpperCase();
    if ("RUB".equals(normalizedBase)) {
      return ResponseEntity.ok(currencyConversionService.getRubRates());
    }

    Map<String, BigDecimal> rates =
        Map.of(
            "RUB", currencyConversionService.getExchangeRate(normalizedBase, "RUB"),
            "USD",
                "USD".equals(normalizedBase)
                    ? BigDecimal.ONE
                    : currencyConversionService.getExchangeRate(normalizedBase, "USD"),
            "EUR",
                "EUR".equals(normalizedBase)
                    ? BigDecimal.ONE
                    : currencyConversionService.getExchangeRate(normalizedBase, "EUR"));
    return ResponseEntity.ok(rates);
  }
}
