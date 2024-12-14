package com.example.wealthwise_api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SavingTargets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavingTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long targetId;

    @Column(name = "target_title", nullable = false)
    private String targetTitle;

    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "cyclical_payment_amount")
    private BigDecimal cyclicalPaymentAmount;

    @Column(name = "cyclical_payment_cron")
    private String cyclicalPaymentCron;

    @Column(name = "current_amount", nullable = false)
    private BigDecimal currentAmount;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private UserEntity userEntity;

    public SavingTarget(String targetTitle, BigDecimal targetAmount, LocalDateTime targetDate,
                        BigDecimal cyclicalPaymentAmount, String cyclicalPaymentCron,
                        BigDecimal currentAmount, String description, UserEntity userEntity) {
        this.targetTitle = targetTitle;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.cyclicalPaymentAmount = cyclicalPaymentAmount;
        this.cyclicalPaymentCron = cyclicalPaymentCron;
        this.currentAmount = currentAmount;
        this.creationDate = LocalDateTime.now();
        this.description = description;
        this.userEntity = userEntity;
    }
}
