package com.wex.service.impl;

import com.wex.exception.ExchangeRateNotFoundException;
import com.wex.model.TreasuryRateResponse;
import com.wex.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TreasuryExchangeRateService implements ExchangeRateService {
    private static final Logger log = LoggerFactory.getLogger(TreasuryExchangeRateService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final Map<String, String> CURRENCY_MAPPINGS = Map.of(
        "EUR", "European-Euro",
        "GBP", "United Kingdom-Pound",
        "CAD", "Canada-Dollar",
        "JPY", "Japan-Yen"
        // Add more mappings as needed
    );
    
    private String formatCurrency(String currencyCode) {
        return CURRENCY_MAPPINGS.getOrDefault(currencyCode, currencyCode);
    }
    
    @Value("${treasury.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TreasuryExchangeRateService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
        try {
            // Format the currency according to Treasury API format (e.g., "European-Euro" for "EUR")
            String formattedCurrency = formatCurrency(currency);
            String formattedDate = date.format(DATE_FORMATTER);
            
            String apiUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v1/accounting/od/rates_of_exchange")
                .queryParam("fields", "exchange_rate,record_date,country_currency_desc")
                .queryParam("filter", String.format("record_date:lte:%s,country_currency_desc:eq:%s", formattedDate, formattedCurrency))
                .queryParam("sort", "-record_date")
                .queryParam("page[number]", "1")
                .queryParam("page[size]", "1")
                .build()
                .toUriString();
                
            log.info("Calling Treasury API: {}", apiUrl);

            ResponseEntity<String> rawResponse = restTemplate.getForEntity(apiUrl, String.class);
            log.debug("Raw API Response: {}", rawResponse.getBody());
            
            if (!rawResponse.getStatusCode().is2xxSuccessful()) {
                log.error("API returned error status: {} with body: {}", 
                    rawResponse.getStatusCode(), rawResponse.getBody());
                throw new ExchangeRateNotFoundException("Failed to fetch exchange rate from Treasury API");
            }

            TreasuryRateResponse response = objectMapper.readValue(rawResponse.getBody(), TreasuryRateResponse.class);
            
            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.error("No data found in API response for currency: {} and date: {}", currency, date);
                throw new ExchangeRateNotFoundException("No exchange rate found for " + currency + " on or before " + date);
            }

            TreasuryRateResponse.RateData rateData = response.getData().get(0);
            LocalDate exchangeRateDate = LocalDate.parse(rateData.getRecordDate());
            
            if (exchangeRateDate.isBefore(date.minusMonths(6))) {
                log.error("Exchange rate found is older than 6 months. Found date: {}, Requested date: {}", 
                    exchangeRateDate, date);
                throw new ExchangeRateNotFoundException(
                    String.format("No exchange rate available within 6 months of transaction date. Latest available: %s", 
                        exchangeRateDate));
            }

            log.info("Successfully retrieved exchange rate data: {}", rateData);
            return rateData;
        } catch (Exception e) {
            log.error("Error fetching exchange rate data: ", e);
            throw new ExchangeRateNotFoundException("Error fetching exchange rate: " + e.getMessage());
        }
    }
}