package com.example.wealthwise_api.DTO;

public class AssetsRequestDelete {

    private String currency;
    private String name;
    private double value;

    public AssetsRequestDelete(String currency, String name, double value) {
        this.currency = currency;
        this.name = name;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
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
