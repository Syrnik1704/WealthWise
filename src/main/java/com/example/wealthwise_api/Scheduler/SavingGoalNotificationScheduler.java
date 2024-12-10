package com.example.wealthwise_api.Scheduler;

import com.example.wealthwise_api.Entity.SavingTarget;
import com.example.wealthwise_api.Repository.SavingTargetRepository;
import com.example.wealthwise_api.Services.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SavingGoalNotificationScheduler {

    private final SavingTargetRepository savingTargetRepository;
    private final EmailService emailService;

    public SavingGoalNotificationScheduler(
            SavingTargetRepository savingTargetRepository,
            EmailService emailService) {
        this.savingTargetRepository = savingTargetRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void sendScheduledNotifications() {
        List<SavingTarget> savingTargets = savingTargetRepository.findAll();
        for (SavingTarget target : savingTargets) {
            String cronExpression = target.getCyclicalPaymentCron();

            if (isTimeToSendNotification(cronExpression)) {
                sendSavingGoalNotification(target);
            }
        }
    }

    private boolean isTimeToSendNotification(String cronExpression) {
        try {
            CronExpression cron = CronExpression.parse(cronExpression);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextExecution = cron.next(now);
            return nextExecution != null && nextExecution.isBefore(now.plusHours(1));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid CRON expression: " + cronExpression);
            return false;
        }
    }

    private void sendSavingGoalNotification(SavingTarget target) {
        String userEmail = target.getUserEntity().getEmail();
        try {
            emailService.sendEmail(userEmail, "Saving Goal Summary and Reminder", "static/email_template");
            System.out.println("Powiadomienie o celu oszczędzania wysłane do: " + userEmail);
        } catch (Exception e) {
            System.err.println("Błąd podczas wysyłania powiadomienia o celu oszczędzania do " + userEmail + ": " + e.getMessage());
        }
    }
}
