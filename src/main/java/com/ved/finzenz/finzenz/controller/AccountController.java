package com.ved.finzenz.finzenz.controller;
import com.ved.finzenz.finzenz.dto.AccountResponse;
import com.ved.finzenz.finzenz.entities.Account;
import com.ved.finzenz.finzenz.request.AccountRequest;
import com.ved.finzenz.finzenz.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {


    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }

    //fixed
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAllAccountsforUser(userId);
        List<AccountResponse> responseList = accounts.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId) {
        Account account = accountService.getAccountbyID(accountId);
        return ResponseEntity.ok(toResponse(account));
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<?> getBalance(@PathVariable Long accountId) {
        BigDecimal balance = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/networth/{accountId}")
    public ResponseEntity<?> getNetWorth(@PathVariable Long accountId) {
        BigDecimal networth = accountService.getNetWorth(accountId);
        return ResponseEntity.ok(networth);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        boolean delete = accountService.deleteAccount(accountId);
        if (delete) {
            return ResponseEntity.ok("Account deleted succesfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getAccountId());
        response.setAccountName(account.getAccountName());
        response.setAccountType(account.getAccountType());
        response.setInstitutionName(account.getInstitutionName());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setIsActive(account.getIsActive());
        response.setUserId(account.getUser().getId());
        response.setUserEmail(account.getUser().getEmail());
        return response;
    }
}

