package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserData {
    private long id;
    private String name;
    private String surname;
    private String email;
    private Boolean isActive;
    private Role role;
    private String  birthDay;
}