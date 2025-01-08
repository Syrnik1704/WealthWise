package com.example.wealthwise_api.Scheduler;

import com.example.wealthwise_api.Entity.Subscription;
import com.example.wealthwise_api.Repository.SubscriptionRepository;
import com.example.wealthwise_api.Services.EmailService;
import com.example.wealthwise_api.Services.ExpensesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class SubscriptionNotificationScheduler {

    Logger logger = LoggerFactory.getLogger(SubscriptionNotificationScheduler.class);
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;
    private final ExpensesService expensesService;

    public SubscriptionNotificationScheduler(
            SubscriptionRepository subscriptionRepository,
            EmailService emailService,
            ExpensesService expensesService) {
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
        this.expensesService = expensesService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run every midnight; Run every 2 minutes for testing purpose is "0 */2 * * * ?"
    public void processSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        LocalDate today = LocalDate.now();
        int todayDayOfMonth = today.getDayOfMonth();
        int daysInMonth = YearMonth.from(today).lengthOfMonth();

        for (Subscription subscription : subscriptions) {
            if (subscription.getCyclicalPaymentDate() != null) {
                int paymentDay = subscription.getCyclicalPaymentDate();

                // If paymentDay is valid for the current month
                if (paymentDay <= daysInMonth && paymentDay == todayDayOfMonth) {
                    handleSubscriptionPayment(subscription);
                }
                // If paymentDay exceeds the number of days in the current month, process on the last day
                else if (paymentDay > daysInMonth && todayDayOfMonth == daysInMonth) {
                    handleSubscriptionPayment(subscription);
                }
            }
        }
    }

    private void handleSubscriptionPayment(Subscription subscription) {
        try {
            // Add expense for the subscription
            expensesService.addExpenseForSubscription(
                    subscription.getUserEntity().getIdUser(),
                    subscription.getAmount().doubleValue(),
                    "Subscription payment for: " + subscription.getSubscriptionTitle()
            );

            String userEmail = subscription.getUserEntity().getEmail();
            emailService.sendSubscriptionNotification(
                    userEmail,
                    "Recurring Subscription Payment",
                    "static/email_subscription_notification.html",
                    subscription
            );

            logger.info("Subscription payment processed and notification sent to: " + userEmail);
        } catch (Exception e) {
            logger.error("Error processing subscription: " + subscription.getSubscriptionTitle() + ": " + e.getMessage());
        }
    }
}
