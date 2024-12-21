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

    @Scheduled(cron = "0 */5 * * * *") // Uruchamianie co 5 minut
    public void sendScheduledNotifications() {
        List<SavingTarget> savingTargets = savingTargetRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (SavingTarget target : savingTargets) {
            String cronExpression = target.getCyclicalPaymentCron();

            if (isTimeToSendNotification(cronExpression, now)) {
                sendSavingGoalNotification(target);
            }
        }
    }

    private boolean isTimeToSendNotification(String cronExpression, LocalDateTime now) {
        try {
            CronExpression cron = CronExpression.parse(cronExpression);
            LocalDateTime nextExecution = cron.next(now.minusMinutes(1));
            return nextExecution != null && nextExecution.isBefore(now.plusMinutes(5));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid CRON expression: " + cronExpression);
            return false;
        }
    }

    private void sendSavingGoalNotification(SavingTarget target) {
        String userEmail = target.getUserEntity().getEmail();
        try {
            emailService.sendSavingGoalNotification(
                    userEmail,
                    "Saving Goal Summary and Reminder",
                    "static/email_saving_goal_notification.html",
                    target
            );
            System.out.println("Notification about saving goal sent to " + userEmail);
        } catch (Exception e) {
            System.err.println("Failed to send notification about saving goal to " + userEmail + ": " + e.getMessage());
        }
    }
}
