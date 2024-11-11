package com.angubaidullin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleUpdateDTO {
    private Set<String> rolesToAdd;
    private Set<String> rolesToRemove;
}
