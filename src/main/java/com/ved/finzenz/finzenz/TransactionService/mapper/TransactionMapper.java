package com.ved.finzenz.finzenz.TransactionService.mapper;

import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.TransactionService.dto.TransactionResponse;
import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.TransactionService.request.TransactionRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request, Account account) {
        return Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .transactionDate(
                        request.getTransactionDate() != null ?
                                request.getTransactionDate() :
                                LocalDateTime.now()
                )
                .description(request.getDescription())
                .transactionType(request.getTransactionType())
                .category(request.getCategory())
                .build();
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .transactionType(transaction.getTransactionType())
                .category(transaction.getCategory())
                .build();
    }
}