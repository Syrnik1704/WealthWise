package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.AuthenticationRequest;
import com.example.wealthwise_api.DTO.AuthenticationRequestToken;
import com.example.wealthwise_api.DTO.UserRegistrationRequest;
import com.example.wealthwise_api.Services.AuthenticationService;
import com.example.wealthwise_api.Services.EmailService;
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
    private final EmailService emailService;

    public AuthenticationController(AuthenticationService authenticationService, RegistrationService registrationService, EmailService emailService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request){
        return authenticationService.login(request);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request){
        ResponseEntity<Object> responseEntity = registrationService.register(request);
        if (responseEntity.getStatusCode() == HttpStatus.OK){
            emailService.sendEmail(request.email(), "Welcome in WealthWise Application!", "static/email_welcome_notification.html");
        }
        return responseEntity;
    }

    @PostMapping(value = "refreshToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequestToken refreshToken){
        return authenticationService.refreshToken(refreshToken);
    }

}