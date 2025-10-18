package com.wex.controller;

import com.wex.dto.PurchaseRequest;
import com.wex.dto.PurchaseResponse;
import com.wex.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    @Test
    void createPurchase_ValidRequest_ReturnsCreatedPurchase() throws Exception {
        PurchaseResponse mockResponse = new PurchaseResponse();
        mockResponse.setId("123");
        mockResponse.setDescription("Test Purchase");
        mockResponse.setOriginalAmount(new BigDecimal("100.00"));

        when(purchaseService.createPurchase(any(PurchaseRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test Purchase\",\"transactionDate\":\"2025-10-15\",\"amount\":100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.description").value("Test Purchase"));
    }

    @Test
    void createPurchase_InvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"\",\"transactionDate\":\"2025-10-15\",\"amount\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPurchaseInCurrency_ValidRequest_ReturnsConvertedPurchase() throws Exception {
        PurchaseResponse mockResponse = new PurchaseResponse();
        mockResponse.setId("123");
        mockResponse.setOriginalAmount(new BigDecimal("100.00"));
        mockResponse.setConvertedAmount(new BigDecimal("85.00"));
        mockResponse.setTargetCurrency("EUR");

        when(purchaseService.getPurchaseInCurrency("123", "EUR")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/purchases/123/convert")
                .param("currency", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.convertedAmount").value(85.00))
                .andExpect(jsonPath("$.targetCurrency").value("EUR"));
    }
}