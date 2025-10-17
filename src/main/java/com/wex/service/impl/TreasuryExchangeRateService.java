package com.wex.service.impl;

import com.wex.exception.ExchangeRateNotFoundException;
import com.wex.model.TreasuryRateResponse;
import com.wex.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TreasuryExchangeRateService implements ExchangeRateService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final Map<String, String> CURRENCY_MAPPINGS = Map.of(
        "EUR", "European-Euro",
        "GBP", "United Kingdom-Pound",
        "CAD", "Canada-Dollar",
        "JPY", "Japan-Yen"
    );
    
    private String formatCurrency(String currencyCode) {
        return CURRENCY_MAPPINGS.getOrDefault(currencyCode, currencyCode);
    }
    
    @Value("${treasury.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public TreasuryExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BigDecimal getExchangeRate(String currency, LocalDate date) {
        TreasuryRateResponse.RateData rateData = getRateData(currency, date);
        return new BigDecimal(rateData.getExchangeRate());
    }

    @Override
    public LocalDate getExchangeRateDate(String currency, LocalDate date) {
        TreasuryRateResponse.RateData rateData = getRateData(currency, date);
        return LocalDate.parse(rateData.getRecordDate());
    }

    private TreasuryRateResponse.RateData getRateData(String currency, LocalDate date) {
        String formattedCurrency = formatCurrency(currency);
        String formattedDate = date.format(DATE_FORMATTER);
        
        String apiUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/v1/accounting/od/rates_of_exchange")
            .queryParam("fields", "exchange_rate,record_date,country_currency_desc")
            .queryParam("filter", String.format("record_date:lte:%s,country_currency_desc:eq:%s", formattedDate, formattedCurrency))
            .queryParam("sort", "-record_date")
            .queryParam("page[size]", "1")
            .build()
            .toUriString();

        ResponseEntity<TreasuryRateResponse> response = restTemplate.getForEntity(apiUrl, TreasuryRateResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null 
            || response.getBody().getData() == null || response.getBody().getData().isEmpty()) {
            throw new ExchangeRateNotFoundException("No exchange rate found for " + currency + " on or before " + date);
        }

        TreasuryRateResponse.RateData rateData = response.getBody().getData().get(0);
        LocalDate exchangeRateDate = LocalDate.parse(rateData.getRecordDate());
        
        if (exchangeRateDate.isBefore(date.minusMonths(6))) {
            throw new ExchangeRateNotFoundException(
                String.format("No exchange rate available within 6 months of transaction date. Latest available: %s", 
                    exchangeRateDate));
        }

        return rateData;
    }
}