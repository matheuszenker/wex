package com.wex.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseRequest {
    @NotBlank
    @Size(max = 50)
    private String description;
    
    @NotNull
    private LocalDate transactionDate;
    
    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;
}