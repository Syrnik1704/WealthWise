package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.DTO.IncomesResponse;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Services.IncomesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/incomes")
public class IncomeController {
    private IncomesService incomesService;

    public IncomeController(IncomesService incomesService) {
        this.incomesService = incomesService;
    }

    @PostMapping(value="/addIncome")
    public ResponseEntity<?> addIncome(HttpServletRequest request,@RequestBody IncomesRequest incomesRequest){
        return incomesService.addIncome(request,incomesRequest);
    }

    @GetMapping(value="/getIncomes")
    public ResponseEntity<List<Incomes>> getAllIncomesOfUser(HttpServletRequest request){
        return incomesService.getAllIncomesOfUser(request);
    }

    @GetMapping(value="{incomeId}")
    public ResponseEntity<Incomes> getIncomeById(HttpServletRequest request, @PathVariable Long incomeId){
        return incomesService.getIncome(request,incomeId);
    }

    @DeleteMapping(value="/deleteIncome")
    public ResponseEntity<String> deleteIncome(HttpServletRequest request, @RequestBody List<Long> incomesId){
        return incomesService.deleteIncomes(request,incomesId);
    }

    @PutMapping(value="/updateIncome/{incomeId}")
    public ResponseEntity<String> updateIncome(HttpServletRequest request, @PathVariable Long incomeId, @RequestBody IncomesRequest incomesRequest){
        return incomesService.updateIncome(request,incomeId,incomesRequest);
    }
}
