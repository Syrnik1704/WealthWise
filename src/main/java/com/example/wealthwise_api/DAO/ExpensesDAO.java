package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Expenses;
import java.util.Date;
import java.util.List;

public interface ExpensesDAO {

    void save(Expenses expenses);

    void delete(Expenses expenses);

    List<Expenses> findExpensesByUser(long userID);

    Expenses findExpensesByUserAndIdExpenses(long userID, long idExpenses);

    boolean checkExpensesExists(List<Long> idExpenses);

    List<Expenses> findExpensesInDateRangeAndSelectedCategories(long userId, Date startDate, Date endDate, long categoriesId);
}
