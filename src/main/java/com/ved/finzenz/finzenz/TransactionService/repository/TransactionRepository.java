package com.ved.finzenz.finzenz.TransactionService.repository;


import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.TransactionService.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccountUserId(Integer userId);

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccountUserIdAndTransactionDateBetween(
            Integer userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount),0)
        FROM Transaction t
        WHERE t.account.user.id = :userId
        AND LOWER(t.category) = LOWER(:category)
        AND t.transactionType = 'DEBIT'
    """)
    BigDecimal getTotalSpendingByCategory(Integer userId, String category);

    @Query("""
        SELECT COALESCE(SUM(t.amount),0)
        FROM Transaction t
        WHERE t.account.user.id = :userId
        AND t.transactionType = 'CREDIT'
    """)
    BigDecimal getTotalIncome(Integer userId);

    @Query("""
        SELECT COALESCE(SUM(t.amount),0)
        FROM Transaction t
        WHERE t.account.user.id = :userId
        AND t.transactionType = 'DEBIT'
    """)
    BigDecimal getTotalExpense(Integer userId);

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.user.id = :userId
    AND MONTH(t.transactionDate) = :month
    AND YEAR(t.transactionDate) = :year
    """)
    List<Transaction> findUserMonthlyTransactions(Integer userId, int month, int year);

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.user.id = :userId
    AND LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Transaction> searchByDescription(Integer userId, String keyword);

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.user.id = :userId
    AND t.transactionDate BETWEEN :startDate AND :endDate
    """)
    List<Transaction> findByUserIdAndDateRange(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );


    Page<Transaction> findByAccountUserId(Integer userId, Pageable pageable);
}


