package com.example.wealthwise_api.Scheduler;

import com.example.wealthwise_api.Entity.SavingTarget;
import com.example.wealthwise_api.Repository.SavingTargetRepository;
import com.example.wealthwise_api.Services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SavingGoalNotificationScheduler {

    Logger logger = LoggerFactory.getLogger(SavingGoalNotificationScheduler.class);
    private final SavingTargetRepository savingTargetRepository;
    private final EmailService emailService;

    public SavingGoalNotificationScheduler(
            SavingTargetRepository savingTargetRepository,
            EmailService emailService) {
        this.savingTargetRepository = savingTargetRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void sendScheduledNotifications() {
        List<SavingTarget> savingTargets = savingTargetRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (SavingTarget target : savingTargets) {

            if (now.isBefore(target.getTargetDate()) &&
                    (target.getCurrentAmount().doubleValue() < target.getTargetAmount().doubleValue())) {

                String cronExpression = target.getCyclicalPaymentCron();

                if (isTimeToSendNotification(cronExpression, now)) {
                    sendSavingGoalNotification(target);
                }
            }
        }
    }

    private boolean isTimeToSendNotification(String cronExpression, LocalDateTime now) {
        try {
            CronExpression cron = CronExpression.parse(cronExpression);
            LocalDateTime nextExecution = cron.next(now.minusMinutes(1));
            return nextExecution != null && nextExecution.isBefore(now.plusMinutes(5));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid CRON expression: " + cronExpression);
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
            logger.info("Notification about saving goal sent to " + userEmail);
        } catch (Exception e) {
            logger.error("Failed to send notification about saving goal to " + userEmail + ": " + e.getMessage());
        }
    }
}
