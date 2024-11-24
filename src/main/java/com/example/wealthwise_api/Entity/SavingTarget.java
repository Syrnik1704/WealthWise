package com.example.wealthwise_api.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SavingTargets")
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

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private UserEntity userEntity;

    public SavingTarget() {
    }

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

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDateTime getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDateTime targetDate) {
        this.targetDate = targetDate;
    }

    public BigDecimal getCyclicalPaymentAmount() {
        return cyclicalPaymentAmount;
    }

    public void setCyclicalPaymentAmount(BigDecimal cyclicalPaymentAmount) {
        this.cyclicalPaymentAmount = cyclicalPaymentAmount;
    }

    public String getCyclicalPaymentCron() {
        return cyclicalPaymentCron;
    }

    public void setCyclicalPaymentCron(String cyclicalPaymentCron) {
        this.cyclicalPaymentCron = cyclicalPaymentCron;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
