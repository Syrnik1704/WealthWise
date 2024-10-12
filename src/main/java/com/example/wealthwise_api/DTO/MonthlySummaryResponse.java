package com.example.wealthwise_api.DTO;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;

public class MonthlySummaryResponse {
    private String months;
    private double totalIncome;
    private double totalExpenses;

    public MonthlySummaryResponse(String months, double totalIncome, double totalExpenses) {
        this.months = months;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
    }

    public String getMonth() {
        return months;
    }

    public void setMonth(String months) {
        this.months = months;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}
