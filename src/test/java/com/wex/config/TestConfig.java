package com.wex.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mockito;
import com.wex.model.TreasuryRateResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}