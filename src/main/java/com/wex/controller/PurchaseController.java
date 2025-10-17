package com.wex.controller;

import com.wex.dto.PurchaseRequest;
import com.wex.dto.PurchaseResponse;
import com.wex.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.createPurchase(request));
    }

    @GetMapping("/{id}/convert")
    public ResponseEntity<PurchaseResponse> getPurchaseInCurrency(
            @PathVariable String id,
            @RequestParam String currency) {
        return ResponseEntity.ok(purchaseService.getPurchaseInCurrency(id, currency));
    }
}