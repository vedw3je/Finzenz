package com.ved.finzenz.finzenz.AccountService.service;

import com.ved.finzenz.finzenz.AccountService.dto.AccountResponse;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.request.AccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest request);
    AccountResponse updateAccount(AccountRequest request, Long accountId);
    AccountResponse getAccountById(Long accountId);
    BigDecimal getAccountBalance(Long accountId);
    List<AccountResponse> getAccountsByUser(Long userId);
    BigDecimal getNetWorth(Long UserID);
    void deleteAccount(Long accountId);

}