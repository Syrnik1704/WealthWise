package com.example.wealthwise_api.Repository;


import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Entity.Incomes;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses,Long> {

    @Query(value = "SELECT e FROM Expenses e WHERE e.userEntity.idUser = :userId")
    List<Expenses> findExpensesByUser(@Param("userId") long userId);

    @Query(value = "SELECT e FROM Expenses e WHERE e.userEntity.idUser = :userId AND e.idExpenses = :idExpenses")
    Expenses findExpensesByUserAndIdExpenses(@Param("userId") long userId, @Param("idExpenses") long idExpenses);

    @Query(value = "SELECT CASE WHEN COUNT(e) = :size THEN true ELSE false END " +
            "FROM Expenses e WHERE e.idExpenses IN :idExpenses")
    boolean checkExpensesExists(List<Long> idExpenses, long size);

    @Query(value = "SELECT e FROM Expenses e WHERE e.userEntity.idUser = :userId AND e.createdDate BETWEEN :startDate AND :endDate AND e.category.idCategories = :categoriesId")
    List<Expenses> findExpensesInDateRangeAndSelectedCategories(long userId, Date startDate, Date endDate, long categoriesId);

    // Wydatki utworzone w danym miesiącu przez użytkownika
    @Query("SELECT e FROM Expenses e " +
            "WHERE MONTH(e.createdDate) = :month AND YEAR(e.createdDate) = :year " +
            "AND e.userEntity.idUser = :userId")
    List<Expenses> findCreatedInMonth(int month, int year, Long userId);
}
