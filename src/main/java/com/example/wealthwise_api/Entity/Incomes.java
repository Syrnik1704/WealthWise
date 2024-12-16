package com.example.wealthwise_api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
@Table(name = "incomes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Incomes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idIncomes;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private Date createdDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUser",nullable = false)
    private UserEntity userEntity;

    public Incomes(String name, String description, double value, Date createdDate, UserEntity userEntity) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.createdDate = createdDate;
        this.userEntity = userEntity;
    }

}
