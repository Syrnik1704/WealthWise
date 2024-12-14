package com.example.wealthwise_api.DTO;


import com.example.wealthwise_api.Entity.Categories;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@NotNull
public class ExpensesRequest {
    private String name;
    private String description;
    private double amount;
    private String categoryName;
}
