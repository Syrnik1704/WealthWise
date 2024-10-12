package com.example.wealthwise_api.DTO;

public record AuthenticationResponse(String tokenAccess,
                                     String tokenRefresh) {
}