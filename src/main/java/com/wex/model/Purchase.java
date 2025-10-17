package com.wex.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(length = 50, nullable = false)
    private String description;
    
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}