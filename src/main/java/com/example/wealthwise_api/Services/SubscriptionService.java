package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DTO.SubscriptionRequest;
import com.example.wealthwise_api.DTO.SubscriptionResponse;
import com.example.wealthwise_api.Entity.Subscription;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Exception.ResourceNotFoundException;
import com.example.wealthwise_api.Repository.SubscriptionRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final UserEntityRepository userEntityRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public SubscriptionService(SubscriptionRepository repository, UserEntityRepository userEntityRepository, JWTUtil jwtUtil) {
        this.repository = repository;
        this.userEntityRepository = userEntityRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<SubscriptionResponse> getSubscriptions(String token) {
        UserEntity user = findUserByToken(token);
        return repository.findByUserEntity_IdUser(user.getIdUser()).stream()
                .map(SubscriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse getSubscriptionById(String token, Long subscriptionId) {
        UserEntity user = findUserByToken(token);
        Subscription subscription = findSubscriptionById(subscriptionId);

        if (subscription.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to view this subscription.");
        }

        return SubscriptionResponse.fromEntity(subscription);
    }

    public SubscriptionResponse addSubscription(String token, SubscriptionRequest request) {
        UserEntity user = findUserByToken(token);
        Subscription subscription = request.toEntity(user);
        subscription.setCreationDate(LocalDateTime.now());
        Subscription saved = repository.save(subscription);
        return SubscriptionResponse.fromEntity(saved);
    }

    public void deleteSubscription(String token, Long subscriptionId) {
        UserEntity user = findUserByToken(token);
        Subscription subscription = findSubscriptionById(subscriptionId);

        if (subscription.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to delete this subscription.");
        }

        repository.delete(subscription);
    }

    public SubscriptionResponse updateSubscription(String token, Long subscriptionId, SubscriptionRequest request) {
        UserEntity user = findUserByToken(token);
        Subscription subscription = findSubscriptionById(subscriptionId);

        if (subscription.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to update this subscription.");
        }

        if (request.getSubscriptionTitle() != null) {
            subscription.setSubscriptionTitle(request.getSubscriptionTitle());
        }
        if (request.getDescription() != null) {
            subscription.setDescription(request.getDescription());
        }
        if (request.getAmount() != null) {
            subscription.setAmount(request.getAmount());
        }
        if (request.getCyclicalPaymentDate() != null) {
            subscription.setCyclicalPaymentDate(request.getCyclicalPaymentDate());
        }

        Subscription updated = repository.save(subscription);
        return SubscriptionResponse.fromEntity(updated);
    }

    private UserEntity findUserByToken(String token) {
        String email = jwtUtil.getEmail(token.replace("Bearer ", ""));
        UserEntity user = userEntityRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User associated with the token was not found.");
        }
        return user;
    }

    private Subscription findSubscriptionById(Long subscriptionId) {
        return repository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription with the specified ID was not found."));
    }
}
