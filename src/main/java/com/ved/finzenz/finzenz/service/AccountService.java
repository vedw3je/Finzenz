package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.dto.AccountResponse;
import com.ved.finzenz.finzenz.entities.Account;
import com.ved.finzenz.finzenz.request.AccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest request);
    Account getAccountbyID(Long accountId);
    BigDecimal getAccountBalance(Long accountId);
    List<Account> getAllAccountsforUser(Long userID);
    BigDecimal getNetWorth(Long UserID);
    boolean deleteAccount(Long accountId);

}