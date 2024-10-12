package com.example.wealthwise_api.DTO;

public record AuthenticationRequest (String email,
                                     String password) {
}