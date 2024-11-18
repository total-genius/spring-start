package com.angubaidullin.controller;

import com.angubaidullin.dto.*;
import com.angubaidullin.service.AccountService;
import com.angubaidullin.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/account/info")
    public ResponseEntity<AccountResponseUserDTO> getCurrentAccount() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (currentUserEmail == null) {
            throw new SecurityException("No authenticated user found");
        }

        return ResponseEntity.ok(accountService.getCurrentAccount(currentUserEmail));
    }

    //по пути /accounts/** доступ получает только пользователь с ролью ADMIN
    //Получение пользователя по id
    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountResponseAdminDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findAccountById(id));
    }

    //Получение всех пользователей
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponseAdminDTO>> getAll() {
        return ResponseEntity.ok(accountService.findAllAccounts());
    }

    //Удаление пользователя по id
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<AccountResponseAdminDTO> deleteAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.deleteAccount(id));
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
//    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponseAdminDTO> saveListOfAccounts(@RequestBody List<AccountCreateDTO> accountCreateDTOList) {
        return accountCreateDTOList.stream()
                .map(accountCreateDTO -> accountService.createAccount(accountCreateDTO))
                .toList();
    }
}
