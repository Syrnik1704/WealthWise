package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.AuthenticationFailedResponse;
import com.example.wealthwise_api.DTO.RegistrationResponse;
import com.example.wealthwise_api.DTO.UserRegistrationRequest;
import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.Entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final EmailValidatorService emailValidatorService;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidatorService passwordValidatorService;


    public RegistrationService(EmailValidatorService emailValidatorService, UserDAO userDAO,
                               PasswordEncoder passwordEncoder, PasswordValidatorService passwordValidatorService) {
        this.emailValidatorService = emailValidatorService;
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidatorService = passwordValidatorService;
    }

    public ResponseEntity<Object> register(UserRegistrationRequest request){

        if(request.email().isEmpty() || request.password().isEmpty() || request.confirmPassword().isEmpty() || request.name().isEmpty() || request.surname().isEmpty() || request.birthDay().isEmpty()){
            return  new ResponseEntity<>( new AuthenticationFailedResponse("Please fill all fields!"), HttpStatus.BAD_REQUEST);
        }

        if(!emailValidatorService.test(request.email())){
            return  new ResponseEntity<>( new AuthenticationFailedResponse("Email is not valid!"), HttpStatus.BAD_REQUEST);
        }

        if(userDAO.existsUserWithEmail(request.email())){
            return  new ResponseEntity<>( new AuthenticationFailedResponse("Email is taken!"), HttpStatus.BAD_REQUEST);
        }

        if(!passwordValidatorService.test(request.password())){
            return  new ResponseEntity<>( new AuthenticationFailedResponse("Password is not valid!"), HttpStatus.BAD_REQUEST);
        }

        if(!request.password().equals(request.confirmPassword())){
            return  new ResponseEntity<>( new AuthenticationFailedResponse("Passwords do not match!"), HttpStatus.BAD_REQUEST);
        }


        UserEntity userEntity = new UserEntity(
            request.email(), passwordEncoder.encode(request.confirmPassword()), request.name(), request.surname(),request.birthDay(), Role.USER
        );

        userDAO.save(userEntity);

        return new ResponseEntity<>( new RegistrationResponse("User " + userEntity.getUsername() + " registered successfully")
                ,HttpStatus.OK);
    }

}