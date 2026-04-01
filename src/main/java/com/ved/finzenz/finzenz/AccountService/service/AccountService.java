package com.ved.finzenz.finzenz.AccountService.service;

import com.ved.finzenz.finzenz.AccountService.dto.AccountResponse;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.request.AccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest request);
    AccountResponse updateAccount(AccountRequest request, Long accountId);
    Account getAccountbyID(Long accountId);
    BigDecimal getAccountBalance(Long accountId);
    List<Account> getAllAccountsforUser(Long userID);
    BigDecimal getNetWorth(Long UserID);
    boolean deleteAccount(Long accountId);

}