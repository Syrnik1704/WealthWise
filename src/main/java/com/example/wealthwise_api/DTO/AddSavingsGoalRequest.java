package com.example.wealthwise_api.DTO;

public class AddSavingsGoalRequest {
    private String token;
    private String name;
    private double currentAmount;

    public AddSavingsGoalRequest(String token, String name, double currentAmount) {
        this.token = token;
        this.name = name;
        this.currentAmount = currentAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
