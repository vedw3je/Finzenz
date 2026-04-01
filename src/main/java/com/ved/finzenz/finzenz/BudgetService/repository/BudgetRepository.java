package com.ved.finzenz.finzenz.BudgetService.repository;

import com.ved.finzenz.finzenz.BudgetService.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    List<Budget> findByUserId(Integer userId);

    List<Budget> findByUserIdAndCategoryIgnoreCase(Integer userId, String category);

    @Query("""
        SELECT COALESCE(SUM(b.amount), 0)
        FROM Budget b
        WHERE b.user.id = :userId
        AND LOWER(b.category) = LOWER(:category)
        AND b.startDate <= :date
        AND b.endDate >= :date
    """)
    BigDecimal getActiveBudgetTotal(Integer userId, String category, LocalDate date);
}
