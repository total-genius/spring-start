package com.angubaidullin.dto;

import com.angubaidullin.entity.Account;
import com.angubaidullin.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountRequestDTO {
    private String name;
    private String email;
    private List<Bill> bills;


}
