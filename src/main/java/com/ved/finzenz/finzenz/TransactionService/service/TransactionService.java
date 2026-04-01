package com.ved.finzenz.finzenz.TransactionService.service;



import com.ved.finzenz.finzenz.TransactionService.dto.TransactionResponse;
import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.TransactionService.request.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    // ------------------- CRUD -------------------
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    TransactionResponse getTransactionById(Integer id);
    TransactionResponse updateTransaction(Integer id, TransactionRequest transactionRequest);
    void deleteTransaction(Integer id);

    // ------------------- Finance App Specific -------------------
    List<TransactionResponse> getTransactionsByAccountId(Long accountId);

    // Get all transactions for all accounts of a user (native query)
    List<TransactionResponse> getTransactionsByUser(Integer userId);
    List<TransactionResponse> getUserMonthlyTransactions(Integer userId, int month, int year);


    // Get all transactions for a user in a date range
    List<TransactionResponse> getTransactionsByUserIdAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate);


    BigDecimal getTotalSpendingByCategory(Integer userId, String category);


    BigDecimal getTotalIncome(Integer userId);


    BigDecimal getTotalExpense(Integer userId);


    List<TransactionResponse> searchTransactionsByDescriptionForUser(Integer userId, String keyword);

    Page<TransactionResponse> getTransactionsPaginated(Integer userId, Pageable pageable);

    BigDecimal getNetBalance(Integer userId);
}
