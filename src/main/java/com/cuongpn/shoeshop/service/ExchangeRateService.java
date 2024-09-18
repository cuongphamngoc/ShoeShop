package com.cuongpn.shoeshop.service;

import java.math.BigDecimal;

public interface ExchangeRateService {
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency);
}