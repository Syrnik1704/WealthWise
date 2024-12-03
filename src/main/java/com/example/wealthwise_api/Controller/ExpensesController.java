package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.ExpensesRequest;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.ExpensesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/expenses")
public class ExpensesController {
    private final ExpensesService expensesService;

    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @PostMapping(value = "/saveExpense",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveExpenses(@RequestBody ExpensesRequest expensesRequest) {
        return expensesService.saveExpenses(expensesRequest);
    }

    @GetMapping(value = "/getExpense",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExpenses(HttpServletRequest request){
        return expensesService.getFewLastExpenses(request);
    }

    @GetMapping(value = "/getByCategory",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExpensesForEachCategoryByMonth(HttpServletRequest request){
        return expensesService.getExpensesForEachCategoryByMonth(request);
    }

    @GetMapping(value = "/getMonthlyExpenseAndIncome",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMonthlyExpenseAndIncome(HttpServletRequest request){
        return expensesService.getMonthlyIncome(request);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(
            @RequestHeader("Authorization") String token,
            @PathVariable Long expenseId) {
        expensesService.deleteExpense(token, expenseId);
        return ResponseEntity.ok("Income deleted successfully");
    }

}
