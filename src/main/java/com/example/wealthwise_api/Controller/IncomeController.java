package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.DTO.IncomesResponse;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.IncomesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/incomes")
public class IncomeController {
    private IncomesService incomesService;

    public IncomeController(IncomesService incomesService) {
        this.incomesService = incomesService;
    }

    @GetMapping(value="/getIncome",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getIncomes(HttpServletRequest request){
        return incomesService.getMonthlyIncome(request);
    }

    @PostMapping(value="/addIncome",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addIncome(@RequestBody IncomesRequest incomesRequest){
        return incomesService.addIncome(incomesRequest);
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<String> deleteIncome(
            @RequestHeader("Authorization") String token,
            @PathVariable Long incomeId) {
        incomesService.deleteIncome(token, incomeId);
        return ResponseEntity.ok("Income deleted successfully");
    }
}
