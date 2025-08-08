package com.ved.finzenz.finzenz.repository;


import com.ved.finzenz.finzenz.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Native query: find all transactions by userId
    @Query(
            value = "SELECT * FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = :userId",
            nativeQuery = true
    )
    List<Transaction> findByUserId(@Param("userId") Integer userId);

    // Find all transactions for an account
    List<Transaction> findByAccountId(Integer accountId);

    // Find all transactions for an account within a date range
    List<Transaction> findByAccountIdAndTransactionDateBetween(
            Integer accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Find all transactions for an account and type
    List<Transaction> findByAccountIdAndTransactionType(
            Integer accountId,
            Transaction.TransactionType transactionType
    );

    // Search by description keyword
    List<Transaction> findByAccountIdAndDescriptionContainingIgnoreCase(
            Integer accountId,
            String keyword
    );
}
