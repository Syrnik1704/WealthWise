package com.example.wealthwise_api.Entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
@Table(name = "incomes")
public class Incomes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idIncomes;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "idUser",nullable = false)
    private UserEntity userEntity;

    public Incomes(@NotNull double value, @NotNull Date createdDate, @NotNull UserEntity userEntity) {
        this.value = value;
        this.createdDate = createdDate;
        this.userEntity = userEntity;
    }

    public Incomes() {
    }

    public long getIdIncomes() {
        return idIncomes;
    }

    public void setIdIncomes(long idIncomes) {
        this.idIncomes = idIncomes;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
