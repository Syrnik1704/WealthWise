package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserEntity_IdUser(Long idUser);

    // Subskrypcje utworzone w danym miesiącu przez użytkownika
    @Query("SELECT s FROM Subscription s " +
            "WHERE MONTH(s.creationDate) = :month AND YEAR(s.creationDate) = :year " +
            "AND s.userEntity.idUser = :userId")
    List<Subscription> findCreatedInMonth(int month, int year, Long userId);

    // Aktywne subskrypcje użytkownika
    @Query("SELECT s FROM Subscription s " +
            "WHERE s.userEntity.idUser = :userId")
    List<Subscription> findActiveSubscriptions(Long userId);
}
