package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.DTO.ExpensesResponse;
import com.example.wealthwise_api.DTO.MonthlySummaryResponse;
import com.example.wealthwise_api.Entity.Expenses;
import jakarta.persistence.Tuple;

import java.util.List;

public interface ExpensesDAO {

    void save(Expenses expenses);
    boolean exists(long userId);
    List<Expenses> getExpensesByUserId(Long userId);
    List<Tuple> getExpensesForEachCategoryByMonth(Long userId);
    double getSumOfExpensesByUserId(Long userId);
    List<Tuple> getMonthlySummary(Long userId);

}
