package com.example.wealthwise_api.Controllers;

import com.example.wealthwise_api.DTO.SubscriptionRequest;
import com.example.wealthwise_api.DTO.SubscriptionResponse;
import com.example.wealthwise_api.Services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions(
            @RequestHeader("Authorization") String token) {
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptions(token);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        SubscriptionResponse subscription = subscriptionService.getSubscriptionById(token, id);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @RequestHeader("Authorization") String token,
            @Validated @RequestBody SubscriptionRequest request) {
        SubscriptionResponse createdSubscription = subscriptionService.addSubscription(token, request);
        return ResponseEntity.ok(createdSubscription);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Validated @RequestBody SubscriptionRequest request) {
        SubscriptionResponse updatedSubscription = subscriptionService.updateSubscription(token, id, request);
        return ResponseEntity.ok(updatedSubscription);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        subscriptionService.deleteSubscription(token, id);
        return ResponseEntity.noContent().build();
    }
}
