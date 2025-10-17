package com.wex.service.impl;

import com.wex.dto.PurchaseRequest;
import com.wex.dto.PurchaseResponse;
import com.wex.exception.PurchaseNotFoundException;
import com.wex.model.Purchase;
import com.wex.repository.PurchaseRepository;
import com.wex.service.ExchangeRateService;
import com.wex.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Purchase purchase = new Purchase();
        purchase.setDescription(request.getDescription());
        purchase.setTransactionDate(request.getTransactionDate());
        purchase.setAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP));
        
        purchase = purchaseRepository.save(purchase);
        
        return convertToResponse(purchase, "USD", BigDecimal.ONE, purchase.getTransactionDate());
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseInCurrency(String purchaseId, String currency) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
            .orElseThrow(() -> new PurchaseNotFoundException("Purchase not found with ID: " + purchaseId));

        LocalDate exchangeRateDate = exchangeRateService.getExchangeRateDate(currency, purchase.getTransactionDate());
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(currency, exchangeRateDate);
        
        return convertToResponse(purchase, currency, exchangeRate, exchangeRateDate);
    }

    private PurchaseResponse convertToResponse(Purchase purchase, String currency, BigDecimal exchangeRate, LocalDate exchangeRateDate) {
        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getId());
        response.setDescription(purchase.getDescription());
        response.setTransactionDate(purchase.getTransactionDate());
        response.setOriginalAmount(purchase.getAmount());
        response.setTargetCurrency(currency);
        response.setExchangeRate(exchangeRate);
        response.setExchangeRateDate(exchangeRateDate);
        response.setConvertedAmount(purchase.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
        return response;
    }
}