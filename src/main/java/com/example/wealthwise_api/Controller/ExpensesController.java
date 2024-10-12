package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.ExpensesRequest;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.ExpensesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

    @PostMapping(value = "/getExpense",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExpenses(@RequestBody TokenRequest tokenRequest){
        return expensesService.getFewLastExpenses(tokenRequest);
    }

    @PostMapping(value = "/getByCategory",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExpensesForEachCategoryByMonth(@RequestBody TokenRequest tokenRequest){
        return expensesService.getExpensesForEachCategoryByMonth(tokenRequest);
    }

    @PostMapping(value = "/getMonthlyExpenseAndIncome",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMonthlyExpenseAndIncome(@RequestBody TokenRequest tokenRequest){
        return expensesService.getMonthlyIncome(tokenRequest);
    }

}
