package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long subscriptionId;
    private String subscriptionTitle;
    private BigDecimal amount;
    private Integer cyclicalPaymentDate;
    private String description;
    private LocalDateTime creationDate;

    public static SubscriptionResponse fromEntity(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getSubscriptionId(),
                subscription.getSubscriptionTitle(),
                subscription.getAmount(),
                subscription.getCyclicalPaymentDate(),
                subscription.getDescription(),
                subscription.getCreationDate()
        );
    }
}
