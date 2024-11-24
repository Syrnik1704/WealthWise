package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.SavingTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingTargetRepository extends JpaRepository<SavingTarget, Long> {
    List<SavingTarget> findByUserEntity_IdUser(Long userId);
}
