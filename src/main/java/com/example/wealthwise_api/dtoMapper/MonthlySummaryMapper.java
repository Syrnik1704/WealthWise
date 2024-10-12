package com.example.wealthwise_api.dtoMapper;

import com.example.wealthwise_api.DTO.MonthlySummaryResponse;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Component;

@Component
public class MonthlySummaryMapper {
    public MonthlySummaryResponse mapToMonthlySummaryResponse(Tuple tuple) {
        String month = tuple.get("months", String.class);
        if(tuple.get("totalIncome", Double.class) == null || tuple.get("totalExpenses", Double.class) == null || tuple.get("months", String.class) == null) {
            return new MonthlySummaryResponse(null, 0.0, 0.0);
        }
        Double totalIncome = tuple.get("totalIncome", Double.class);
        Double totalExpenses = tuple.get("totalExpenses", Double.class);

        return new MonthlySummaryResponse(month, totalIncome, totalExpenses);
    }
}
