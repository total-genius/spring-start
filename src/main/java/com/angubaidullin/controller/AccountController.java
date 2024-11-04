package com.angubaidullin.controller;

import com.angubaidullin.dto.AccountRequestDTO;
import com.angubaidullin.dto.AccountResponseDTO;
import com.angubaidullin.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/accounts")
    public AccountResponseDTO createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(accountRequestDTO);
    }

    @PostMapping("/accounts/save/all")
    public List<AccountResponseDTO> saveListOfAccounts(@RequestBody List<AccountRequestDTO> accountRequestDTOList) {
        return accountRequestDTOList.stream()
                .map(accountRequestDTO -> accountService.createAccount(accountRequestDTO))
                .toList();
    }


    @GetMapping("accounts/{id}")
    public AccountResponseDTO getAccountById(@PathVariable Long id) {
        return accountService.findAccountById(id);
    }

    @GetMapping("accounts/account")
    public List<AccountResponseDTO> getAccountsByName(@RequestParam String name) {
        return accountService.findAllAccountsByName(name);
    }

    @GetMapping("accounts")
    public List<AccountResponseDTO> getAll() {
        return accountService.findAllAccounts();
    }

    @DeleteMapping("accounts/{id}")
    public AccountResponseDTO deleteAccountById(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }
}
