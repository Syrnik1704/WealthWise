package com.example.wealthwise_api.DTO;

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
public class IncomesRequest {
    private String name;
    private String description;
    private double value;
}
