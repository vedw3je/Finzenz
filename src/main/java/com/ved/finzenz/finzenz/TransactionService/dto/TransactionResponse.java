package com.ved.finzenz.finzenz.TransactionService.dto;



import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.TransactionService.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private Integer id;
    private Long accountId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
    private TransactionType transactionType;
    private String category;


    public TransactionResponse(Transaction created) {
        this.id = created.getId();
        this.accountId = created.getAccount().getId();
        this.amount = created.getAmount();
        this.transactionDate = created.getTransactionDate();
        this.description = created.getDescription();
        this.transactionType = created.getTransactionType();
        this.category = created.getCategory();
    }
}
