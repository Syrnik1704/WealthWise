package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomesRepository extends JpaRepository<Incomes, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(i) = :size THEN true ELSE false END " +
            "FROM Incomes i WHERE i.idIncomes IN :idIncomes")
    boolean checkIncomesExists(@Param("idIncomes") List<Long> idIncomes, @Param("size") long size);

    @Query(value = "SELECT i FROM Incomes i WHERE i.userEntity.idUser = :userId")
    List<Incomes> findIncomesByUser(@Param("userId") long userId);

    @Query(value = "SELECT i FROM Incomes i WHERE i.userEntity.idUser = :userId AND i.idIncomes = :idIncomes")
    Incomes findIncomesByUserAndIdIncomes(@Param("userId") long userId, @Param("idIncomes") long idIncomes);

    // Dochody utworzone w danym miesiącu przez użytkownika
    @Query("SELECT i FROM Incomes i " +
            "WHERE MONTH(i.createdDate) = :month AND YEAR(i.createdDate) = :year " +
            "AND i.userEntity.idUser = :userId")
    List<Incomes> findCreatedInMonth(int month, int year, Long userId);
}
