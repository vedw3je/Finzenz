package com.ved.finzenz.finzenz.request;

import com.ved.finzenz.finzenz.entities.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TransactionRequest {

    @NotNull(message = "Account ID cannot be null")
    private Integer accountId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    // Optional: allow frontend to pass a date or default to now
    private LocalDateTime transactionDate;

    private String description;

    @NotNull(message = "Transaction type cannot be null")
    private Transaction.TransactionType transactionType;

    private String category;
}

