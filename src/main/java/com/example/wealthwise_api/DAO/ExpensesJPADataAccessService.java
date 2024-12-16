package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Repository.ExpensesRepository;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
    public void delete(Expenses expenses) {
        expensesRepository.delete(expenses);
    }

    @Override
    public List<Expenses> findExpensesByUser(long userID) {
        return expensesRepository.findExpensesByUser(userID);
    }

    @Override
    public Expenses findExpensesByUserAndIdExpenses(long userID, long idExpenses) {
        return expensesRepository.findExpensesByUserAndIdExpenses(userID, idExpenses);
    }

    @Override
    public boolean checkExpensesExists(List<Long> idExpenses) {
        return expensesRepository.checkExpensesExists(idExpenses, idExpenses.size());
    }

    @Override
    public List<Expenses> findExpensesInDateRangeAndSelectedCategories(long userId, Date startDate, Date endDate, long categoriesId) {
        return expensesRepository.findExpensesInDateRangeAndSelectedCategories(userId, startDate, endDate, categoriesId);
    }
}
