package com.example.wealthwise_api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "expenses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Expenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idExpenses;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Date createdDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUser",nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "idCategory",nullable = false)
    private Categories category;

    public Expenses(String name, String description ,double amount, Date createdDate, UserEntity userEntity, Categories category) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.createdDate = createdDate;
        this.userEntity = userEntity;
        this.category = category;
    }
}
