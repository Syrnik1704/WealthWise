package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.SavingsGoals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsGoalsRepository extends JpaRepository<SavingsGoals, Long> {
    @Query(value = "SELECT EXISTS(SELECT * FROM savings_goals WHERE name =:goalName AND id_user =:userId)", nativeQuery = true)
    boolean existsByGoalNameAndUserId(@Param("goalName") String goalName,@Param("userId") Long userId);
    @Query(value = "SELECT * FROM savings_goals WHERE name =:nameGoal AND id_user =:userId", nativeQuery = true)
    SavingsGoals findByGoalName(@Param("nameGoal") String nameGoal ,@Param("userId") Long userId);
    @Query(value = "SELECT * FROM savings_goals WHERE id_user =:userId AND status = 'true'", nativeQuery = true)
    List<SavingsGoals> findSavingsGoalsListByUserId(@Param("userId")Long userID);

}
