package com.example.wealthwise_api.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.wealthwise_api.DTO.SavingTargetRequest;
import com.example.wealthwise_api.DTO.SavingTargetResponse;
import com.example.wealthwise_api.Services.SavingTargetService;

@RestController
@RequestMapping("/savingTargets")
public class SavingTargetController {

    private final SavingTargetService service;

    public SavingTargetController(SavingTargetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SavingTargetResponse>> getSavingTargets(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.getSavingTargets(token));
    }

    @GetMapping("/{targetId}")
    public ResponseEntity<SavingTargetResponse> getSavingTargetById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(service.getSavingTargetById(token, targetId));
    }

    @PostMapping
    public ResponseEntity<SavingTargetResponse> addSavingTarget(
            @RequestHeader("Authorization") String token,
            @RequestBody SavingTargetRequest request) {
        return ResponseEntity.ok(service.addSavingTarget(token, request));
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<String> deleteSavingTarget(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetId) {
        service.deleteSavingTarget(token, targetId);
        return ResponseEntity.ok("Target deleted successfully");
    }

    @PutMapping("/{targetId}")
    public ResponseEntity<SavingTargetResponse> updateSavingTarget(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetId,
            @RequestBody SavingTargetRequest request) {
        return ResponseEntity.ok(service.updateSavingTarget(token, targetId, request));
    }
}
