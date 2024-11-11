package com.angubaidullin.dto;

import com.angubaidullin.entity.Account;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountResponseAdminDTO {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;

    public AccountResponseAdminDTO(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.roles = account.getRoles();
    }
}
