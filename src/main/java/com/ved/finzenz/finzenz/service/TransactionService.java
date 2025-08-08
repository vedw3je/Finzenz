package com.ved.finzenz.finzenz.service;



import com.ved.finzenz.finzenz.entities.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    // ------------------- CRUD -------------------
    Transaction createTransaction(Transaction transaction);
    Transaction getTransactionById(Integer id);
    Transaction updateTransaction(Integer id, Transaction transaction);
    void deleteTransaction(Integer id);

    // ------------------- Finance App Specific -------------------
    List<Transaction> getTransactionsByAccountId(Integer accountId);

    // Get all transactions for all accounts of a user (native query)
    List<Transaction> getTransactionsByUserId(Integer userId);

    List<Transaction> getTransactionsByDateRange(Integer accountId, LocalDateTime startDate, LocalDateTime endDate);

    // Get all transactions for a user in a date range
    List<Transaction> getTransactionsByUserIdAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalSpendingByCategory(Integer accountId, String category);
    BigDecimal getTotalSpendingByCategoryForUser(Integer userId, String category);

    BigDecimal getTotalIncome(Integer accountId);
    BigDecimal getTotalIncomeForUser(Integer userId);

    BigDecimal getTotalExpense(Integer accountId);
    BigDecimal getTotalExpenseForUser(Integer userId);

    List<Transaction> searchTransactionsByDescription(Integer accountId, String keyword);
    List<Transaction> searchTransactionsByDescriptionForUser(Integer userId, String keyword);
}
