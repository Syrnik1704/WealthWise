package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.Incomes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomesRepository extends JpaRepository<Incomes, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END\n" +
            "FROM Incomes i\n" +
            "WHERE YEAR(i.createdDate) = YEAR(CURRENT_DATE)\n" +
            "  AND MONTH(i.createdDate) = MONTH(CURRENT_DATE)\n" +
            "  AND i.userEntity.idUser = :userId")
    boolean existsForDedicatedMonthAndYear(@Param("userId") long userId);

    @Query(value = "SELECT i\n" +
            "FROM Incomes i\n" +
            "WHERE YEAR(i.createdDate) = YEAR(CURRENT_DATE)\n" +
            "  AND MONTH(i.createdDate) = MONTH(CURRENT_DATE)\n" +
            "  AND i.userEntity.idUser = :userId")
    Incomes findIncomesByUser(@Param("userId") long userId);
}
