package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.SavingTarget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SavingTargetResponse {
    private Long targetId;
    private String targetTitle;
    private BigDecimal targetAmount;
    private LocalDateTime targetDate;
    private BigDecimal cyclicalPaymentAmount;
    private String cyclicalPaymentCron;
    private BigDecimal currentAmount;
    private LocalDateTime creationDate;
    private String description;
    private Long idUser;

    public SavingTargetResponse(Long targetId, String targetTitle, BigDecimal targetAmount, LocalDateTime targetDate,
                                 BigDecimal cyclicalPaymentAmount, String cyclicalPaymentCron, BigDecimal currentAmount,
                                 LocalDateTime creationDate, String description, Long idUser) {
        this.targetId = targetId;
        this.targetTitle = targetTitle;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.cyclicalPaymentAmount = cyclicalPaymentAmount;
        this.cyclicalPaymentCron = cyclicalPaymentCron;
        this.currentAmount = currentAmount;
        this.creationDate = creationDate;
        this.description = description;
        this.idUser = idUser;
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

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public static SavingTargetResponse fromEntity(SavingTarget target) {
        return new SavingTargetResponse(
                target.getTargetId(),
                target.getTargetTitle(),
                target.getTargetAmount(),
                target.getTargetDate(),
                target.getCyclicalPaymentAmount(),
                target.getCyclicalPaymentCron(),
                target.getCurrentAmount(),
                target.getCreationDate(),
                target.getDescription(),
                target.getUserEntity().getIdUser()
        );
    }
}
