package com.ved.finzenz.finzenz.AccountService.dto;

import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.enums.AccountType;
import com.ved.finzenz.finzenz.AccountService.enums.CurrencyType;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String accountName;
    private AccountType accountType;
    private String institutionName;
    private String accountNumber;
    private BigDecimal balance;
    private CurrencyType currency;
    private Boolean isActive;
    private Long userId;
    private String userEmail;


}