package com.angubaidullin.dto;

import com.angubaidullin.entity.Account;
import com.angubaidullin.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountResponseUserDTO {
    private String name;
    private String email;
    private List<Bill> bills;
    private Set<String> roles;

    public AccountResponseUserDTO(Account account) {
        this.name = account.getName();
        this.email = account.getEmail();
        this.bills = account.getBills();
        this.roles = account.getRoles();
    }
}
