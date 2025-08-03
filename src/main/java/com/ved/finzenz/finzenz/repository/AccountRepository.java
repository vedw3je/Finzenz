package com.ved.finzenz.finzenz.repository;

import com.ved.finzenz.finzenz.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
}

