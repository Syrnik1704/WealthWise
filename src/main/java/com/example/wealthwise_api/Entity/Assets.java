package com.example.wealthwise_api.Entity;


import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;


@Entity
@Table(name = "assets")
public class Assets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idAssets;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "idUser",nullable = false)
    private UserEntity userEntity;

    public Assets(@NotNull double value, @NotNull String currency, @NotNull String name, @NotNull UserEntity userEntity) {
        this.value = value;
        this.currency = currency;
        this.name = name;
        this.userEntity = userEntity;
    }

    public Assets() {
    }

    public long getIdAssets() {
        return idAssets;
    }

    public void setIdAssets(long idAssets) {
        this.idAssets = idAssets;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }


}
