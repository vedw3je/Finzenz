package com.ved.finzenz.finzenz.repository;

import com.ved.finzenz.finzenz.entities.Budget;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUserId(Integer userId);
    List<Budget> findByCategory(String category);
    List<Budget> findByUserIdAndCategoryIgnoreCase(Integer userId, String category);
    @Query("""
    SELECT b FROM Budget b
    WHERE b.userId = :userId
      AND LOWER(b.category) = LOWER(:category)
      AND b.startDate <= :date
      AND b.endDate >= :date
""")
    List<Budget> findActiveBudgets(Integer userId, String category, LocalDate date);

}
