package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service

public class ExchangeRateServiceImpl implements ExchangeRateService {
    @Value("${openexchangerates.api.key}")
    private String apiKey;
    private static final String EXCHANGE_RATE_URL = "https://openexchangerates.org/api/latest.json?app_id=";

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        String url = EXCHANGE_RATE_URL + apiKey;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> rates = (Map<String, Object>) response.getBody().get("rates");
        BigDecimal fromRate = new BigDecimal(rates.get(fromCurrency).toString());
        System.out.println(fromRate);
        BigDecimal toRate = new BigDecimal(rates.get(toCurrency).toString());
        System.out.println(toRate);
        System.out.println(toRate.divide(fromRate, 10, RoundingMode.HALF_UP));
        return toRate.divide(fromRate, 10, RoundingMode.HALF_UP);
    }
}
