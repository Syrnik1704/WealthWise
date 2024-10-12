package com.example.wealthwise_api.Entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Entity
@Table(name = "categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCategories;

    @Column(nullable = false,unique = true)
    private String name;

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Expenses> expenses ;

    public Categories(@NotNull String name) {
        this.name = name;
    }

    public Categories() {
    }

    public long getIdCategories() {
        return idCategories;
    }

    public void setIdCategories(long idCategories) {
        this.idCategories = idCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
