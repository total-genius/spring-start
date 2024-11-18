package com.angubaidullin.dto;

import com.angubaidullin.entity.Account;
import com.angubaidullin.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@ToString
@AllArgsConstructor
public class KafkaDTO {
    private Long id;
    private String name;
    private String email;
    private List<Bill> bills;
    private Set<String> roles;

    public KafkaDTO(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.bills = account.getBills();
        this.roles = account.getRoles();
    }
}
