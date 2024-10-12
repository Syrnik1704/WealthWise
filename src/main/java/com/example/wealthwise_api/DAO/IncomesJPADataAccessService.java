package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Repository.IncomesRepository;
import org.springframework.stereotype.Repository;

@Repository("incomesJPA")
public class IncomesJPADataAccessService implements IncomesDAO{
    private final IncomesRepository incomesRepository;

    public IncomesJPADataAccessService(IncomesRepository incomesRepository) {
        this.incomesRepository = incomesRepository;
    }

    @Override
    public void save(Incomes incomes) {
        incomesRepository.save(incomes);
    }

    @Override
    public void delete(Incomes incomes) {
        incomesRepository.delete(incomes);
    }

    @Override
    public boolean existsForDedicatedMonth(long userID) {
        return incomesRepository.existsForDedicatedMonthAndYear(userID);
    }
    @Override
    public Incomes findIncomesByUser(long userID) {
        return incomesRepository.findIncomesByUser(userID);
    }
}
