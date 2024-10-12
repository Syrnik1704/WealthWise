package com.example.wealthwise_api.DTO;

public class AssetsRequest{

    private String token;
    private String currency;
    private String name;
    private double value;

    public AssetsRequest(String token, String currency, String name, double value) {
        this.token = token;
        this.currency = currency;
        this.name = name;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
