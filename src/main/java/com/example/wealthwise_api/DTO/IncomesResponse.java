package com.example.wealthwise_api.DTO;

public class IncomesResponse {
    private double value;

    public IncomesResponse(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
