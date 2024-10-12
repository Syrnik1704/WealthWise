package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.DTO.ExpensesResponse;
import com.example.wealthwise_api.DTO.MonthlySummaryResponse;
import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Repository.ExpensesRepository;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;

@Repository("expensesJPA")
public class ExpensesJPADataAccessService implements ExpensesDAO{

    private final ExpensesRepository expensesRepository;

    public ExpensesJPADataAccessService(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }

    @Override
    public void save(Expenses expenses) {
        expensesRepository.save(expenses);
    }

    @Override
    public boolean exists(long userId) {
        return expensesRepository.checkIfExpensesExistInCurrentMonth(userId);
    }
    @Override
    public List<Expenses> getExpensesByUserId(Long userId) {
        return expensesRepository.getFewLastExpensesByUserId(userId);
    }

    @Override
    public List<Tuple> getExpensesForEachCategoryByMonth(Long userId) {
        return expensesRepository.getExpensesForEachCategoryByMonth(userId);
    }

    @Override
    public double getSumOfExpensesByUserId(Long userId) {
        return expensesRepository.getSumOfExpensesByUserId(userId);
    }

    @Override
    public List<Tuple> getMonthlySummary(Long userId) {
        return expensesRepository.getMonthlySummary(userId);
    }
}
