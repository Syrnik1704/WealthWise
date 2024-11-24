package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.SavingTarget;
import com.example.wealthwise_api.Entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SavingTargetRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String targetTitle;

    @NotNull
    private BigDecimal targetAmount;

    private LocalDateTime targetDate;

    private BigDecimal cyclicalPaymentAmount;

    private String cyclicalPaymentCron;

    private BigDecimal currentAmount;

    private String description;

    public SavingTargetRequest(String targetTitle, BigDecimal targetAmount, LocalDateTime targetDate, BigDecimal cyclicalPaymentAmount, String cyclicalPaymentCron, BigDecimal currentAmount, String description) {
        this.targetTitle = targetTitle;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.cyclicalPaymentAmount = cyclicalPaymentAmount;
        this.cyclicalPaymentCron = cyclicalPaymentCron;
        this.currentAmount = currentAmount;
        this.description = description;
    }

    public SavingTargetRequest() {
    }

    public SavingTarget toEntity(UserEntity user) {
        return new SavingTarget(
                targetTitle,
                targetAmount,
                targetDate,
                cyclicalPaymentAmount,
                cyclicalPaymentCron,
                currentAmount,
                description,
                user
        );
    }

    // Gettery i settery (bez creationDate)
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
