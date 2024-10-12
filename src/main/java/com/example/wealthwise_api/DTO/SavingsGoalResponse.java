package com.example.wealthwise_api.DTO;

public class SavingsGoalResponse {
    private String title;
    private double currentAmount;
    private double targetAmount;
    private boolean active;

    public SavingsGoalResponse(String title, double currentAmount, double targetAmount, boolean active) {
        this.title = title;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
