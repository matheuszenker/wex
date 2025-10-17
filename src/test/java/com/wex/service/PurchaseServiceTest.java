package com.wex.service;

import com.wex.dto.PurchaseRequest;
import com.wex.dto.PurchaseResponse;
import com.wex.exception.PurchaseNotFoundException;
import com.wex.model.Purchase;
import com.wex.repository.PurchaseRepository;
import com.wex.service.impl.PurchaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseServiceImpl(purchaseRepository, exchangeRateService);
    }

    @Test
    void createPurchase_ValidRequest_ReturnsSavedPurchase() {
        // Arrange
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test Purchase");
        request.setTransactionDate(LocalDate.now());
        request.setAmount(new BigDecimal("100.00"));

        Purchase savedPurchase = new Purchase();
        savedPurchase.setId("123");
        savedPurchase.setDescription(request.getDescription());
        savedPurchase.setTransactionDate(request.getTransactionDate());
        savedPurchase.setAmount(request.getAmount());

        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        // Act
        PurchaseResponse response = purchaseService.createPurchase(request);

        // Assert
        assertNotNull(response);
        assertEquals("123", response.getId());
        assertEquals("Test Purchase", response.getDescription());
        assertEquals(new BigDecimal("100.00"), response.getOriginalAmount());
    }

    @Test
    void getPurchaseInCurrency_ExistingPurchase_ReturnsConvertedPurchase() {
        // Arrange
        String purchaseId = "123";
        String targetCurrency = "EUR";
        LocalDate purchaseDate = LocalDate.now();
        BigDecimal exchangeRate = new BigDecimal("0.85");

        Purchase purchase = new Purchase();
        purchase.setId(purchaseId);
        purchase.setDescription("Test Purchase");
        purchase.setTransactionDate(purchaseDate);
        purchase.setAmount(new BigDecimal("100.00"));

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(exchangeRateService.getExchangeRateDate(targetCurrency, purchaseDate)).thenReturn(purchaseDate);
        when(exchangeRateService.getExchangeRate(targetCurrency, purchaseDate)).thenReturn(exchangeRate);

        // Act
        PurchaseResponse response = purchaseService.getPurchaseInCurrency(purchaseId, targetCurrency);

        // Assert
        assertNotNull(response);
        assertEquals(purchaseId, response.getId());
        assertEquals(new BigDecimal("100.00"), response.getOriginalAmount());
        assertEquals(new BigDecimal("85.00"), response.getConvertedAmount());
        assertEquals(targetCurrency, response.getTargetCurrency());
    }

    @Test
    void getPurchaseInCurrency_NonExistingPurchase_ThrowsException() {
        // Arrange
        String purchaseId = "non-existing";
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PurchaseNotFoundException.class,
                () -> purchaseService.getPurchaseInCurrency(purchaseId, "EUR"));
    }
}