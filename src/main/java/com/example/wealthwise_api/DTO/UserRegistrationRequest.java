package com.example.wealthwise_api.DTO;

import java.time.LocalDate;

public record UserRegistrationRequest(String name, String surname, String birthDay,String email, String password, String confirmPassword) {

}
