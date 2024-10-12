package com.example.wealthwise_api.DTO;

public record ChangePasswordRequest (String token, String password,String confirmPassword){}
