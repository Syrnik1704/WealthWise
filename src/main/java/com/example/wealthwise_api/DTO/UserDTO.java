package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.Role;

public record UserDTO(
        String email,
        Role role
) {
}