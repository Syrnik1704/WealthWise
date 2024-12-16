package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Incomes;

import java.util.List;

public interface IncomesDAO {

    void save(Incomes incomes);

    void delete(Incomes incomes);

    List<Incomes> findIncomesByUser(long userID);

    Incomes findIncomesByUserAndIdIncomes(long userID, long idIncomes);

    void deleteIncomesById(long idIncomes);

    boolean checkIncomesExists(List<Long> idIncomes);
}
