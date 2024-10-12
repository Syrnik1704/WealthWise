package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.DTO.IncomesResponse;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.IncomesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incomes")
public class IncomeController {
    private IncomesService incomesService;

    public IncomeController(IncomesService incomesService) {
        this.incomesService = incomesService;
    }

    @PostMapping(value="/getIncome",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getIncomes(@RequestBody TokenRequest tokenRequest){
        return incomesService.getMonthlyIncome(tokenRequest);
    }

    @PostMapping(value="/addIncome",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addIncome(@RequestBody IncomesRequest incomesRequest){
        return incomesService.addIncome(incomesRequest);
    }
}
