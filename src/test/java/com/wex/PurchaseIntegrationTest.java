package com.wex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.controller.PurchaseController;
import com.wex.dto.PurchaseRequest;
import com.wex.dto.PurchaseResponse;
import com.wex.service.ExchangeRateService;
import com.wex.service.PurchaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
@ActiveProfiles("test")
class PurchaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    void createAndRetrievePurchase_Success() throws Exception {
        // Setup mock responses
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test Purchase");
        request.setTransactionDate(LocalDate.now());
        request.setAmount(new BigDecimal("100.00"));

        PurchaseResponse createResponse = new PurchaseResponse();
        createResponse.setId("test-id");
        createResponse.setDescription("Test Purchase");
        createResponse.setOriginalAmount(new BigDecimal("100.00"));

        when(purchaseService.createPurchase(any(PurchaseRequest.class))).thenReturn(createResponse);

        PurchaseResponse convertResponse = new PurchaseResponse();
        convertResponse.setId("test-id");
        convertResponse.setDescription("Test Purchase");
        convertResponse.setOriginalAmount(new BigDecimal("100.00"));
        convertResponse.setConvertedAmount(new BigDecimal("85.00"));
        convertResponse.setTargetCurrency("EUR");

        when(purchaseService.getPurchaseInCurrency("test-id", "EUR")).thenReturn(convertResponse);

        // Create Purchase
        mockMvc.perform(post("/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.description").value("Test Purchase"))
                .andExpect(jsonPath("$.originalAmount").value(100.0));

        // Get Purchase with converted currency
        mockMvc.perform(get("/api/purchases/{id}/convert", "test-id")
                .param("currency", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.description").value("Test Purchase"))
                .andExpect(jsonPath("$.originalAmount").value(100.00))
                .andExpect(jsonPath("$.convertedAmount").value(85.00))
                .andExpect(jsonPath("$.targetCurrency").value("EUR"));
    }

    @Test
    void createPurchase_InvalidAmount_ReturnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test Purchase");
        request.setTransactionDate(LocalDate.now());
        request.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPurchase_NonexistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/purchases/{id}", "nonexistent-id")
                .param("currency", "EUR"))
                .andExpect(status().isNotFound());
    }
}