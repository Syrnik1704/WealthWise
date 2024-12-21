package com.example.wealthwise_api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @Column(name = "subscription_title", nullable = false)
    private String subscriptionTitle;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "cyclical_payment_date", nullable = false)
    private Integer cyclicalPaymentDate;

    @Min(1)
    @Max(31)
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private UserEntity userEntity;

    public Subscription(String subscriptionTitle, BigDecimal amount, Integer cyclicalPaymentDate,
                        String description, UserEntity userEntity) {
        this.subscriptionTitle = subscriptionTitle;
        this.amount = amount;
        this.cyclicalPaymentDate = cyclicalPaymentDate;
        this.creationDate = LocalDateTime.now();
        this.description = description;
        this.userEntity = userEntity;
    }
}
