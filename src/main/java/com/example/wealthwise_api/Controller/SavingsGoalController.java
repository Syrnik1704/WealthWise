package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.AddSavingsGoalRequest;
import com.example.wealthwise_api.DTO.SavingsGoalRequest;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.SavingsGoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/savingsGoal")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @PostMapping(value="/createSavingsGoal",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSavingsGoal(@RequestBody SavingsGoalRequest savingsGoalRequest){
        return savingsGoalService.createSavingsGoal(savingsGoalRequest);
    }

    @PostMapping(value="/addCashSavingsGoal",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeCurrentAmount(@RequestBody AddSavingsGoalRequest addSavingsGoalRequest){
        return savingsGoalService.addCashSavingsGoal(addSavingsGoalRequest);
    }

    @GetMapping(value="/getSavingsGoal",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSavingsGoal(HttpServletRequest request){
        return savingsGoalService.getSavingsGoalList(request);
    }
}
