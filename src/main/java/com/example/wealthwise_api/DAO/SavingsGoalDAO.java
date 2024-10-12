package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.SavingsGoals;

import java.util.List;

public interface SavingsGoalDAO {

    void save(SavingsGoals savingsGoal);

    void delete(SavingsGoals savingsGoal);

    boolean checkIfSavingsGoalExists(String goalName , Long userId);

    SavingsGoals getSavingsGoalByName(String goalName, Long userId);

    List<SavingsGoals> getSavingsGoalsByUserId(Long userId);

}
