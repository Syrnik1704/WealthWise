package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.SavingTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingTargetRepository extends JpaRepository<SavingTarget, Long> {
    List<SavingTarget> findByUserEntity_IdUser(Long userId);

    // Cele oszczędnościowe utworzone w danym miesiącu przez użytkownika
    @Query("SELECT st FROM SavingTarget st " +
            "WHERE MONTH(st.creationDate) = :month AND YEAR(st.creationDate) = :year " +
            "AND st.userEntity.idUser = :userId")
    List<SavingTarget> findCreatedInMonth(int month, int year, Long userId);

    // Niedokończone cele oszczędnościowe w danym miesiącu przez użytkownika
    @Query("SELECT st FROM SavingTarget st " +
            "WHERE (st.currentAmount < st.targetAmount OR st.targetDate IS NULL) " +
            "AND (st.targetDate IS NULL OR (YEAR(st.targetDate) > :year OR (YEAR(st.targetDate) = :year AND MONTH(st.targetDate) > :month))) " +
            "AND st.userEntity.idUser = :userId")
    List<SavingTarget> findIncompleteActiveTargets(int month, int year, Long userId);

    // Cele oszczędnościowe zakończone w danym miesiącu przez użytkownika
    @Query("SELECT st FROM SavingTarget st " +
            "WHERE MONTH(st.targetDate) = :month AND YEAR(st.targetDate) = :year " +
            "AND st.currentAmount >= st.targetAmount " +
            "AND st.userEntity.idUser = :userId")
    List<SavingTarget> findCompletedInMonth(int month, int year, Long userId);

    // Cele oszczędnościowe, które nie zostały osiągnięte w danym miesiącu przez użytkownika
    @Query("SELECT st FROM SavingTarget st " +
            "WHERE MONTH(st.targetDate) = :month AND YEAR(st.targetDate) = :year " +
            "AND st.currentAmount < st.targetAmount " +
            "AND st.userEntity.idUser = :userId")
    List<SavingTarget> findFailedTargetsInMonth(int month, int year, Long userId);
}
