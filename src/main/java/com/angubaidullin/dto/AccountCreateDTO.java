package com.angubaidullin.dto;

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
public class AccountCreateDTO {
    private String name;
    private String email;
    private String password;
    private List<Bill> bills;
    private Set<String> roles;
}
