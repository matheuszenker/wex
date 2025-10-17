package com.wex.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExchangeRateService {
    BigDecimal getExchangeRate(String currency, LocalDate date);
    LocalDate getExchangeRateDate(String currency, LocalDate date);
}