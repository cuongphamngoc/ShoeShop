package com.cuongpn.shoeshop.service;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);
}