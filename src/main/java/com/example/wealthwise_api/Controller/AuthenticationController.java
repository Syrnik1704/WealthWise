package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.AuthenticationRequest;
import com.example.wealthwise_api.DTO.AuthenticationRequestToken;
import com.example.wealthwise_api.DTO.UserRegistrationRequest;
import com.example.wealthwise_api.Services.AuthenticationService;
import com.example.wealthwise_api.Services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;

    public AuthenticationController(AuthenticationService authenticationService, RegistrationService registrationService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
    }

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request){
        return authenticationService.login(request);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request){
        return registrationService.register(request);
    }

    @PostMapping(value = "refreshToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequestToken refreshToken){
        return authenticationService.refreshToken(refreshToken);
    }

}