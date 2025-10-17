package com.wex.service;

import com.wex.dto.PurchaseRequest;
import com.wex.dto.PurchaseResponse;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request);
    PurchaseResponse getPurchaseInCurrency(String purchaseId, String currency);
}