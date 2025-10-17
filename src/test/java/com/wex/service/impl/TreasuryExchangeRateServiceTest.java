package com.wex.service.impl;

import com.wex.exception.ExchangeRateNotFoundException;
import com.wex.model.TreasuryRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreasuryExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private TreasuryExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        exchangeRateService = new TreasuryExchangeRateService(restTemplate);
        ReflectionTestUtils.setField(exchangeRateService, "baseUrl", "https://api.fiscaldata.treasury.gov/services");
    }

    @Test
    void getExchangeRate_ValidRequest_ReturnsRate() {
        // Arrange
        LocalDate date = LocalDate.now();
        String currency = "EUR";
        
        TreasuryRateResponse.RateData rateData = new TreasuryRateResponse.RateData();
        rateData.setExchangeRate("1.2");
        rateData.setRecordDate(date.toString());
        rateData.setCountryCurrencyDesc("European-Euro");
        
        TreasuryRateResponse response = new TreasuryRateResponse();
        response.setData(List.of(rateData));
        
        when(restTemplate.getForEntity(
            ArgumentMatchers.contains("/v1/accounting/od/rates_of_exchange"),
            ArgumentMatchers.eq(TreasuryRateResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        // Act
        BigDecimal rate = exchangeRateService.getExchangeRate(currency, date);

        // Assert
        assertEquals(0, new BigDecimal("1.2").compareTo(rate));
    }

    @Test
    void getExchangeRate_RateOlderThan6Months_ThrowsException() {
        // Arrange
        LocalDate date = LocalDate.now();
        String currency = "EUR";
        LocalDate oldDate = date.minusMonths(7);
        
        TreasuryRateResponse.RateData rateData = new TreasuryRateResponse.RateData();
        rateData.setExchangeRate("1.2");
        rateData.setRecordDate(oldDate.toString());
        rateData.setCountryCurrencyDesc("European-Euro");
        
        TreasuryRateResponse response = new TreasuryRateResponse();
        response.setData(List.of(rateData));
        
        when(restTemplate.getForEntity(
            ArgumentMatchers.contains("/v1/accounting/od/rates_of_exchange"),
            ArgumentMatchers.eq(TreasuryRateResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        // Act & Assert
        assertThrows(ExchangeRateNotFoundException.class, () -> exchangeRateService.getExchangeRate(currency, date));
    }

    @Test
    void getExchangeRate_NoRateFound_ThrowsException() {
        // Arrange
        LocalDate date = LocalDate.now();
        String currency = "INVALID";
        
        TreasuryRateResponse response = new TreasuryRateResponse();
        response.setData(List.of());
        
        when(restTemplate.getForEntity(
            ArgumentMatchers.contains("/v1/accounting/od/rates_of_exchange"),
            ArgumentMatchers.eq(TreasuryRateResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        // Act & Assert
        assertThrows(ExchangeRateNotFoundException.class, () -> exchangeRateService.getExchangeRate(currency, date));
    }
}