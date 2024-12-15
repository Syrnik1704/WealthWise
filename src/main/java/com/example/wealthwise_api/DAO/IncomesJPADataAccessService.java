package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Repository.IncomesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Incomes> findIncomesByUser(long userID) {
        return incomesRepository.findIncomesByUser(userID);
    }

    @Override
    public Incomes findIncomesByUserAndIdIncomes(long userID, long idIncomes) {
        return incomesRepository.findIncomesByUserAndIdIncomes(userID, idIncomes);
    }

    @Override
    public void deleteIncomesById(long idIncomes) {
        incomesRepository.deleteById(idIncomes);
    }

    @Override
    public boolean checkIncomesExists(List<Long> idIncomes) {
        return incomesRepository.checkIncomesExists(idIncomes, idIncomes.size());
    }
}
