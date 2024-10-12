package com.example.wealthwise_api.Entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "expenses")
public class Expenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idExpenses;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Date createdDate;

    @Column(nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "idUser",nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "idCategory",nullable = false)
    private Categories category;

    public Expenses(@NotNull double amount, @NotNull Date createdDate,@NotNull String currency,@NotNull UserEntity userEntity, @NotNull Categories category) {
        this.amount = amount;
        this.createdDate = createdDate;
        this.currency = currency;
        this.userEntity = userEntity;
        this.category = category;
    }

    public Expenses() {
    }

    public long getIdExpenses() {
        return idExpenses;
    }

    public void setIdExpenses(long idExpenses) {
        this.idExpenses = idExpenses;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }
}
