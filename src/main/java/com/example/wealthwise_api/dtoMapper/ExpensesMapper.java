package com.example.wealthwise_api.dtoMapper;

import com.example.wealthwise_api.DTO.ExpensesResponse;
import com.example.wealthwise_api.DTO.NewExpensesResponse;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Component;


@Component
public class ExpensesMapper {

    public NewExpensesResponse mapToExpensesResponse(Tuple tuple) {
        String category = tuple.get("category", String.class);
        Double value = tuple.get("value", Double.class);

        return new NewExpensesResponse(category, value);
    }
}