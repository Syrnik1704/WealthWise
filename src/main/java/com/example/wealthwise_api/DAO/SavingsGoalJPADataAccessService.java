package com.example.wealthwise_api.DAO;


import com.example.wealthwise_api.Entity.SavingsGoals;
import com.example.wealthwise_api.Repository.SavingsGoalsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("savingGoalJPA")
public class SavingsGoalJPADataAccessService implements SavingsGoalDAO {

    private final SavingsGoalsRepository savingsGoalsRepository;

    public SavingsGoalJPADataAccessService(SavingsGoalsRepository savingsGoalsRepository) {
        this.savingsGoalsRepository = savingsGoalsRepository;
    }

    @Override
    public void save(SavingsGoals savingsGoal) {
        savingsGoalsRepository.save(savingsGoal);
    }

    @Override
    public void delete(SavingsGoals savingsGoal) {
        savingsGoalsRepository.delete(savingsGoal);
    }

    @Override
    public boolean checkIfSavingsGoalExists(String goalName, Long userId) {
        return savingsGoalsRepository.existsByGoalNameAndUserId(goalName, userId);
    }

    @Override
    public SavingsGoals getSavingsGoalByName(String goalName, Long userId) {
        return savingsGoalsRepository.findByGoalName(goalName, userId);
    }

    @Override
    public List<SavingsGoals> getSavingsGoalsByUserId(Long userId) {
        return savingsGoalsRepository.findSavingsGoalsListByUserId(userId);
    }


}
