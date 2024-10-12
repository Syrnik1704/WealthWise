package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Incomes;

public interface IncomesDAO {

    void save(Incomes incomes);
    void delete(Incomes incomes);
    boolean existsForDedicatedMonth(long userID);
    Incomes findIncomesByUser(long userID);
}
