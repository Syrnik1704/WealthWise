package com.example.wealthwise_api.Controller;


import com.example.wealthwise_api.DTO.ChangePasswordRequest;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Services.ChangePasswordService;
import com.example.wealthwise_api.Services.IncomesService;
import com.example.wealthwise_api.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/user")
public class UserController {

    private ChangePasswordService changePasswordService;
    private IncomesService incomesService;

    public UserController(ChangePasswordService changePasswordService, IncomesService incomesService) {
        this.incomesService = incomesService;
        this.changePasswordService = changePasswordService;
    }

    @PostMapping(value="/changePassword", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){
        return changePasswordService.changePassword(request);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getUserBalance(HttpServletRequest request) {
        return incomesService.getUserBalance(request);
    }

}
