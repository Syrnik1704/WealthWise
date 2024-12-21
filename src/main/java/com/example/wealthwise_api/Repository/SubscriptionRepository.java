package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserEntity_IdUser(Long idUser);
}
