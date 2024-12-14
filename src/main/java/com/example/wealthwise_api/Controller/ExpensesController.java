package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.ExpensesDateRangeRequest;
import com.example.wealthwise_api.DTO.ExpensesRequest;
import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Services.ExpensesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/expenses")
public class ExpensesController {
    private final ExpensesService expensesService;

    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addExpenses(HttpServletRequest request, @RequestBody ExpensesRequest expensesRequest){
        return expensesService.addExpenses(request, expensesRequest);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Expenses>> getAllExpenses(HttpServletRequest request){
        return expensesService.getAllExpensesOfUsers(request);
    }

    @GetMapping("/get/{expensesId}")
    public ResponseEntity<Expenses> getExpensesById(HttpServletRequest request, @PathVariable Long expensesId){
        return expensesService.getExpenses(request, expensesId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteExpenses(HttpServletRequest request, @RequestBody List<Long> expensesId){
        return expensesService.deleteExpenses(request, expensesId);
    }

    @PutMapping("/update/{expensesId}")
    public ResponseEntity<String> updateExpenses(HttpServletRequest request, @PathVariable Long expensesId ,@RequestBody ExpensesRequest expensesRequest){
        return expensesService.updateExpenses(request, expensesId ,expensesRequest);
    }

    @GetMapping("/getByDateRange")
    public ResponseEntity<List<Expenses>> getExpensesByDateRange(HttpServletRequest request, @RequestBody ExpensesDateRangeRequest expensesDateRangeRequest){
        return expensesService.getExpensesInDateRangeAndSelectedCategories(request, expensesDateRangeRequest);
    }
}
