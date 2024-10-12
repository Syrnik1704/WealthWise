package com.example.wealthwise_api.DTO;

public class SavingsGoalRequest {

    private String token;
    private String goalName;
    private double goalAmount;

    public SavingsGoalRequest(String token, String goalName, double goalAmount) {
        this.token = token;
        this.goalName = goalName;
        this.goalAmount = goalAmount;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(double goalAmount) {
        this.goalAmount = goalAmount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
