package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.Subscription;
import com.example.wealthwise_api.Entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String subscriptionTitle;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Integer cyclicalPaymentDate;

    private String description;

    public Subscription toEntity(UserEntity user) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionTitle(subscriptionTitle);
        subscription.setAmount(amount);
        subscription.setCyclicalPaymentDate(cyclicalPaymentDate);
        subscription.setDescription(description);
        subscription.setUserEntity(user);
        return subscription;
    }
}
