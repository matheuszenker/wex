package com.wex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TreasuryRateResponse {
    private List<RateData> data;
    private Meta meta;

    @Data
    public static class RateData {
        @JsonProperty("exchange_rate")
        private String exchangeRate;
        
        @JsonProperty("record_date")
        private String recordDate;
        
        @JsonProperty("country_currency_desc")
        private String countryCurrencyDesc;
    }

    @Data
    public static class Meta {
        private int count;
        @JsonProperty("total-count")
        private int totalCount;
        @JsonProperty("total-pages")
        private int totalPages;
        private Map<String, String> labels;
        private Map<String, String> dataTypes;
        private Map<String, String> dataFormats;
    }
}