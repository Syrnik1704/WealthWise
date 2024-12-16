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
public class UserInfo {
    private String name;
    private Role role;
}
