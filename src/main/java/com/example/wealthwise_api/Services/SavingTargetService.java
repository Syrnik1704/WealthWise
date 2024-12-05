package com.example.wealthwise_api.Services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
import com.example.wealthwise_api.DTO.SavingTargetRequest;
import com.example.wealthwise_api.DTO.SavingTargetResponse;
import com.example.wealthwise_api.Entity.SavingTarget;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.SavingTargetRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import java.time.LocalDateTime;
import com.example.wealthwise_api.Exception.ResourceNotFoundException;

@Service
public class SavingTargetService {

    private final SavingTargetRepository repository;
    private final UserEntityRepository userEntityRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public SavingTargetService(SavingTargetRepository repository, UserEntityRepository userEntityRepository, JWTUtil jwtUtil) {
        this.repository = repository;
        this.userEntityRepository = userEntityRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<SavingTargetResponse> getSavingTargets(String token) {
        UserEntity user = findUserByToken(token);
        return repository.findByUserEntity_IdUser(user.getIdUser()).stream()
                .map(SavingTargetResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public SavingTargetResponse getSavingTargetById(String token, Long targetId) {
        UserEntity user = findUserByToken(token);
        SavingTarget target = findTargetById(targetId);

        if (target.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to view this saving target.");
        }

        return SavingTargetResponse.fromEntity(target);
    }

    public SavingTargetResponse addSavingTarget(String token, SavingTargetRequest request) {
        UserEntity user = findUserByToken(token);
        SavingTarget target = request.toEntity(user);
        target.setCreationDate(LocalDateTime.now());
        SavingTarget saved = repository.save(target);
        return SavingTargetResponse.fromEntity(saved);
    }

    public void deleteSavingTarget(String token, Long targetId) {
        UserEntity user = findUserByToken(token);
        SavingTarget target = findTargetById(targetId);

        if (target.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to delete this saving target.");
        }

        repository.delete(target);
    }

    public SavingTargetResponse updateSavingTarget(String token, Long targetId, SavingTargetRequest request) {
        UserEntity user = findUserByToken(token);
        SavingTarget target = findTargetById(targetId);

        if (target.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to delete this saving target.");
        }

        if (request.getTargetTitle() != null) {
            target.setTargetTitle(request.getTargetTitle());
        }
        if (request.getDescription() != null) {
            target.setDescription(request.getDescription());
        }
        if (request.getTargetAmount() != null) {
            target.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            target.setTargetDate(request.getTargetDate());
        }
        if (request.getCyclicalPaymentAmount() != null) {
            target.setCyclicalPaymentAmount(request.getCyclicalPaymentAmount());
        }
        if (request.getCyclicalPaymentCron() != null) {
            target.setCyclicalPaymentCron(request.getCyclicalPaymentCron());
        }
        if (request.getCurrentAmount() != null) {
            target.setCurrentAmount(request.getCurrentAmount());
        }

        SavingTarget updated = repository.save(target);
        return SavingTargetResponse.fromEntity(updated);
    }

    private UserEntity findUserByToken(String token) {
        String email = jwtUtil.getEmail(token.replace("Bearer ", ""));
        UserEntity user = userEntityRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User associated with the token was not found.");
        }
        return user;
    }

    private SavingTarget findTargetById(Long targetId) {
        return repository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Saving target with the specified ID was not found."));
    }
}

