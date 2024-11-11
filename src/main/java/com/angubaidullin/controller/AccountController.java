package com.angubaidullin.controller;

import com.angubaidullin.dto.*;
import com.angubaidullin.service.AccountService;
import com.angubaidullin.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //Информация об авторизованном пользователе (Админ, Юзер)
    @GetMapping("account/info")
    public AccountResponseAdminDTO getCurrentAccount() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (currentUserEmail == null) {
            throw new SecurityException("No authenticated user found");
        }

        return accountService.getCurrentAccount(currentUserEmail);
    }

    //Регистрация пользователя
    @PostMapping("/register")
    public String register(@RequestBody AccountRegistrationDTO accountRegistrationDTO) {
        accountService.register(accountRegistrationDTO);
        return "Register successful";
    }

    //по пути /accounts/** доступ получает только пользователь с ролью ADMIN
    //Получение пользователя по id
    @GetMapping("accounts/{id}")
    public AccountResponseAdminDTO getAccountById(@PathVariable Long id) {
        return accountService.findAccountById(id);
    }

    //Получение всех пользователей
    @GetMapping("accounts")
    public List<AccountResponseAdminDTO> getAll() {
        return accountService.findAllAccounts();
    }

    //Удаление пользователя по id
    @DeleteMapping("accounts/{id}")
    public AccountResponseAdminDTO deleteAccountById(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }

    //Обновление ролей пользователя (Админ)
    @PutMapping("/accounts/{id}/roles")
    public ResponseEntity<AccountResponseAdminDTO> updateAccountRoles(
            @PathVariable Long id,
            @RequestBody RoleUpdateDTO roleUpdateDTO
    ) {
        return ResponseEntity.ok(accountService.updateRoles(id, roleUpdateDTO));
    }

    //Обновление данных пользователя
    @PutMapping("/account")
    public ResponseEntity<AccountResponseUserDTO> updateAccount(@RequestBody AccountUpdateDTO accountUpdateDTO) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        AccountResponseUserDTO accountResponseUserDTO = accountService.updateAccount(currentUserEmail, accountUpdateDTO);
        return ResponseEntity.ok(accountResponseUserDTO);
    }

    //Добавление новых счетов
    @PostMapping("/account/bills")
    public ResponseEntity<AccountResponseUserDTO> addBills(@RequestBody BillAddDTO billAddDTO) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        AccountResponseUserDTO accountResponseUserDTO = accountService.addBills(currentUserEmail, billAddDTO);
        return ResponseEntity.ok(accountResponseUserDTO);
    }

    //Методы для добавления изначальных пользователей, если таблица окажется пустой
    @PostMapping("/app/util/create")
    public AccountResponseAdminDTO createAccount(@RequestBody AccountCreateDTO accountCreateDTO) {
        return accountService.createAccount(accountCreateDTO);
    }

    @PostMapping("/app/util/create/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponseAdminDTO> saveListOfAccounts(@RequestBody List<AccountCreateDTO> accountCreateDTOList) {
        return accountCreateDTOList.stream()
                .map(accountCreateDTO -> accountService.createAccount(accountCreateDTO))
                .toList();
    }
}
