package com.example.wealthwise_api.Entity;


import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "savings_goals")
public class SavingsGoals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idSavingsGoals;
    @Column(nullable = false)
    @NotNull
    private String name;
    @Column(nullable = false)
    private double targetAmount;
    @Column(nullable = false)
    private double currentAmount;
    @Column(nullable = false)
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "idUser",nullable = false)
    private UserEntity userEntity;

    public SavingsGoals() {
    }

    public SavingsGoals(@NotNull String name, @NotNull double targetAmount, @NotNull double currentAmount, @NotNull boolean status, @NotNull UserEntity userEntity) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.status = status;
        this.userEntity = userEntity;

    }

    public long getIdSavingsGoals() {
        return idSavingsGoals;
    }

    public void setIdSavingsGoals(long idSavingsGoals) {
        this.idSavingsGoals = idSavingsGoals;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
