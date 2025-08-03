package com.ved.finzenz.finzenz.controller;
import com.ved.finzenz.finzenz.dto.AccountResponse;
import com.ved.finzenz.finzenz.entities.Account;
import com.ved.finzenz.finzenz.entities.User;
import com.ved.finzenz.finzenz.exceptions.AccountNotFoundException;
import com.ved.finzenz.finzenz.exceptions.UserNotFoundException;
import com.ved.finzenz.finzenz.repository.AccountRepository;
import com.ved.finzenz.finzenz.repository.UserRepository;
import com.ved.finzenz.finzenz.request.AccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
           throw new UserNotFoundException("User with ID " + request.getUserId() + " not found");
        }

        Account account = new Account();
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setInstitutionName(request.getInstitutionName());
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());
        account.setIsActive(request.getIsActive());
        account.setUser(userOpt.get());

        Account saved = accountRepository.save(account);
        return ResponseEntity.ok(toResponse(saved));
    }

    // GET accounts for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        List<AccountResponse> responseList = accounts.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " not found"));

        return ResponseEntity.ok(toResponse(account));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " not found"));

        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setInstitutionName(request.getInstitutionName());
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());
        account.setIsActive(request.getIsActive());

        Account updated = accountRepository.save(account);
        return ResponseEntity.ok(toResponse(updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        if (!accountRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        accountRepository.deleteById(id);
        return ResponseEntity.ok("Account deleted");
    }

    // Utility method to convert to response DTO
    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
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

