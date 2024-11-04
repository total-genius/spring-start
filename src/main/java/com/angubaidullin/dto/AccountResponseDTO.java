package com.angubaidullin.dto;

import com.angubaidullin.entity.Account;
import com.angubaidullin.entity.Bill;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountResponseDTO {
    private Long id;
    private String name;
    private String email;
    private List<Bill> bills;

    public AccountResponseDTO(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.bills = account.getBills();
    }
}
