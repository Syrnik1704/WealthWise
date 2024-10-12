package com.example.wealthwise_api.DTO;

public class ExpensesResponse {
    private long category_id;
    private double value;

    public ExpensesResponse(long category_id, double value) {
        this.category_id = category_id;
        this.value = value;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
