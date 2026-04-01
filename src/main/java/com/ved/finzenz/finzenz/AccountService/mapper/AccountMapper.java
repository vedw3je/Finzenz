package com.ved.finzenz.finzenz.AccountService.mapper;

import com.ved.finzenz.finzenz.AccountService.dto.AccountResponse;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.request.AccountRequest;
import com.ved.finzenz.finzenz.UserService.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequest request, User user) {
        return Account.builder()
                .accountName(request.getAccountName())
                .accountType(request.getAccountType())
                .institutionName(request.getInstitutionName())
                .accountNumber(request.getAccountNumber())
                .balance(request.getBalance())
                .currency(request.getCurrency())
                .isActive(true)
                .user(user)
                .build();
    }

    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .institutionName(account.getInstitutionName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .isActive(account.getIsActive())
                .userId(account.getUser().getId())
                .userEmail(account.getUser().getEmail())
                .build();
    }
}