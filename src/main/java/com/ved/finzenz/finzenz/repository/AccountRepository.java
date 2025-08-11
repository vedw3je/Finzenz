package com.ved.finzenz.finzenz.repository;

import com.ved.finzenz.finzenz.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
    Account findByAccountId(Long accountId);
    
    @Query("SELECT a.balance FROM Account a WHERE a.accountId = :accountId")
    BigDecimal findBalanceByAccountId(@Param("accountId") Long accountId);

    boolean deleteAccountByAccountId(Long accountId);

    @Query(value = "SELECT SUM(balance) FROM accounts WHERE user_id = :userId GROUP BY user_id", nativeQuery = true)
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);


}

