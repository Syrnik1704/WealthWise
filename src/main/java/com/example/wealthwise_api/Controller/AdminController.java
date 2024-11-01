package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.DTO.UserDataResponse;
import com.example.wealthwise_api.Services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<UserDataResponse>> getAllUsers(){
        return userService.getAllUsers();
    }


}
