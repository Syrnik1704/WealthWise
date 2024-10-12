package com.example.wealthwise_api.Repository;


import com.example.wealthwise_api.Entity.Expenses;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses,Long> {

    @Query(value = "SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM Expenses i " +
            "WHERE YEAR(i.createdDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(i.createdDate) = MONTH(CURRENT_DATE) " +
            "AND i.userEntity.idUser = :userId")
    boolean checkIfExpensesExistInCurrentMonth(@Param("userId") Long userId);

    @Query(value = "SELECT e FROM Expenses e " +
            "WHERE e.userEntity.idUser = :userId " +
            "ORDER BY e.createdDate  ASC LIMIT 5")
    List<Expenses> getFewLastExpensesByUserId(@Param("userId") Long userId);

    @Query(value= "SELECT c.name AS category, COALESCE(SUM(e.amount), 0) AS value\n" +
            "FROM Categories c\n" +
            "LEFT JOIN Expenses e ON c.idCategories = e.category.idCategories\n" +
            "  AND DATE_TRUNC('month', e.createdDate) = DATE_TRUNC('month', CURRENT_DATE)\n" +
            "  AND e.userEntity.idUser = :userId\n" +
            "GROUP BY c.name, c.idCategories")
    List<Tuple> getExpensesForEachCategoryByMonth(@Param("userId") Long userId);

    @Query(value = "SELECT COALESCE(SUM(e.amount), 0) FROM Expenses e WHERE e.userEntity.idUser = :userId AND DATE_TRUNC('month', e.createdDate) = DATE_TRUNC('month', CURRENT_DATE)")
    double getSumOfExpensesByUserId(@Param("userId") Long userId);


    @Query(nativeQuery = true,value="SELECT to_char(i.created_date, 'Month') AS months,\n" +
            "    i.value AS totalIncome,\n" +
            "    SUM(e.amount) AS totalExpenses\n" +
            "FROM Incomes i\n" +
            "LEFT JOIN Expenses e ON i.id_user = e.id_user\n" +
            "WHERE i.id_user = :userId AND EXTRACT(YEAR FROM i.created_date) = EXTRACT(YEAR FROM CURRENT_DATE)\n" +
            "GROUP BY months,totalIncome")
    List<Tuple> getMonthlySummary(@Param("userId") Long userId);

}
