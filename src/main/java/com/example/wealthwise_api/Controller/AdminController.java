package com.example.wealthwise_api.Controller;
import com.example.wealthwise_api.DTO.CategoryRequest;
import com.example.wealthwise_api.DTO.UserData;
import com.example.wealthwise_api.DTO.UsersListRequest;
import com.example.wealthwise_api.Services.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<UserData>> getAllUsers(HttpServletRequest request){
        return adminService.getAllUsers(request);
    }

    @PutMapping("/block")
    public ResponseEntity<String> blockUsers(HttpServletRequest request,@RequestBody UsersListRequest usersBlockRequest ){
       return adminService.blockUsers(usersBlockRequest,request);
    }

    @PutMapping("/unblock")
    public ResponseEntity<String> unblockUsers(HttpServletRequest request,@RequestBody UsersListRequest usersUnblockRequest){
        return adminService.unblockUsers(usersUnblockRequest, request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request,@RequestBody UsersListRequest userDeleteRequest){
        return adminService.deleteUsers(userDeleteRequest,request);
    }

    @PutMapping("/modifyUserData")
    public ResponseEntity<String> modifyUserData(HttpServletRequest request,@RequestBody UserData userData){
        return adminService.modifyUserData(userData,request);
    }

    @PostMapping("/addCategory")
    public ResponseEntity<String> addCategory(HttpServletRequest request,@RequestBody CategoryRequest categoryRequest){
        return adminService.addCategory(categoryRequest,request);
    }


}
