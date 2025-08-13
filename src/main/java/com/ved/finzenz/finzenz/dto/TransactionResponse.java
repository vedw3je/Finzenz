package com.ved.finzenz.finzenz.dto;



import com.ved.finzenz.finzenz.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Integer id;
    private Long accountId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
    private Transaction.TransactionType transactionType;
    private String category;


    public TransactionResponse(Transaction created) {
        this.id = created.getId();
        this.accountId = created.getAccountId();
        this.amount = created.getAmount();
        this.transactionDate = created.getTransactionDate();
        this.description = created.getDescription();
        this.transactionType = created.getTransactionType();
        this.category = created.getCategory();
    }
}
