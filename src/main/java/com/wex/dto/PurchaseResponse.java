package com.wex.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseResponse {
    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private String targetCurrency;
    private BigDecimal exchangeRate;
    private LocalDate exchangeRateDate;
}