package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.Entity.*;
import com.example.wealthwise_api.Repository.SavingTargetRepository;
import com.example.wealthwise_api.Scheduler.SavingGoalNotificationScheduler;
import com.example.wealthwise_api.config.EmailConfig;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EmailServiceTest {


    @Test
    void sendTestEmailTest() {
        // Given
        EmailConfig emailConfig = new EmailConfig("cosmeticsscraper@gmail.com", "qsiiavgzhjikydme");
        EmailService emailService = new EmailService(emailConfig);
        UserEntity user = new UserEntity();
        user.setEmail("syrnikkonrad@gmail.com");
        boolean emailSentWithSuccess = false;

        // When
        try {
            emailService.sendEmail(user.getEmail(), "Test WealthWise email", "static/email_template.html");
            emailSentWithSuccess = true;
        } catch (Exception e) {
            System.err.println("Błąd podczas wysyłania e-maila:");
            e.printStackTrace();
        }

        // Then
        assertTrue(emailSentWithSuccess, "E-mail nie został wysłany pomyślnie");
    }

    @Test
    void testSendScheduledNotifications() {
        // Given
        EmailConfig emailConfig = new EmailConfig("cosmeticsscraper@gmail.com", "qsiiavgzhjikydme");
        EmailService emailService = new EmailService(emailConfig);

        SavingTargetRepository savingTargetRepository = mock(SavingTargetRepository.class);

        SavingTarget mockTarget = new SavingTarget();
        mockTarget.setCyclicalPaymentCron("0 */5 * * * *");
        mockTarget.setCyclicalPaymentAmount(new BigDecimal(5));
        mockTarget.setTargetTitle("Vacation Fund");
        mockTarget.setDescription("Vacation Fund Description - BleBleBle");
        mockTarget.setCurrentAmount(new BigDecimal(110));
        mockTarget.setTargetAmount(new BigDecimal(1000));

        UserEntity user = new UserEntity();
        user.setEmail("marek.porebski01@gmail.com");
        mockTarget.setUserEntity(user);

        SavingGoalNotificationScheduler scheduler = new SavingGoalNotificationScheduler(savingTargetRepository, emailService);

        // When
        when(savingTargetRepository.findAll()).thenReturn(List.of(mockTarget));

        // Then
        scheduler.sendScheduledNotifications();
    }

    @Test
    void sendTestMonthlyReportEmailTest() {
        // Given
        EmailConfig emailConfig = new EmailConfig("cosmeticsscraper@gmail.com", "qsiiavgzhjikydme");
        EmailService emailService = new EmailService(emailConfig);

        UserEntity user = new UserEntity();
        user.setEmail("syrnikkonrad@gmail.com");
        user.setName("Konrad");
        user.setSurname("Syrnik");

        boolean emailSentWithSuccess = false;

        // Subscriptions
        Subscription createdSubscription = new Subscription();
        createdSubscription.setSubscriptionTitle("Amazon Prime");
        createdSubscription.setAmount(new BigDecimal(12));
        createdSubscription.setCyclicalPaymentDate(20);
        createdSubscription.setCreationDate(LocalDateTime.now().minusDays(10));

        Subscription activeSubscription = new Subscription();
        activeSubscription.setSubscriptionTitle("HBO Max");
        activeSubscription.setAmount(new BigDecimal(15));
        activeSubscription.setCyclicalPaymentDate(10);
        activeSubscription.setCreationDate(LocalDateTime.now().minusMonths(3));

        List<Subscription> createdInMonthSubs = List.of(createdSubscription);
        List<Subscription> activeSubscriptions = List.of(activeSubscription);

        // Incomes
        Incomes income1 = new Incomes("Salary", "Monthly salary", 3000.0, new Date(), user);
        Incomes income2 = new Incomes("Freelancing", "Website project", 1200.0, new Date(), user);

        List<Incomes> incomes = List.of(income1, income2);

        // Expenses
        Expenses expense1 = new Expenses("Groceries", "Weekly groceries", 150.0, new Date(), user, null);
        Expenses expense2 = new Expenses("Electricity Bill", "Monthly electricity payment", 80.0, new Date(), user, null);

        List<Expenses> expenses = List.of(expense1, expense2);

        // Saving Targets
        SavingTarget createdSavingTarget = new SavingTarget();
        createdSavingTarget.setTargetTitle("Car Fund");
        createdSavingTarget.setTargetAmount(new BigDecimal(15000));
        createdSavingTarget.setCurrentAmount(new BigDecimal(3000));
        createdSavingTarget.setDescription("Saving for a new car.");
        createdSavingTarget.setCreationDate(LocalDateTime.now().minusDays(5));

        SavingTarget activeSavingTarget = new SavingTarget();
        activeSavingTarget.setTargetTitle("Vacation Fund");
        activeSavingTarget.setTargetAmount(new BigDecimal(5000));
        activeSavingTarget.setCurrentAmount(new BigDecimal(2000));
        activeSavingTarget.setDescription("Saving for summer vacation.");
        activeSavingTarget.setCreationDate(LocalDateTime.now().minusMonths(2));

        SavingTarget completedSavingTarget = new SavingTarget();
        completedSavingTarget.setTargetTitle("Emergency Fund");
        completedSavingTarget.setTargetAmount(new BigDecimal(10000));
        completedSavingTarget.setCurrentAmount(new BigDecimal(10000));
        completedSavingTarget.setDescription("Fully funded emergency savings.");
        completedSavingTarget.setCreationDate(LocalDateTime.now().minusMonths(6));

        SavingTarget failedSavingTarget = new SavingTarget();
        failedSavingTarget.setTargetTitle("Laptop Fund");
        failedSavingTarget.setTargetAmount(new BigDecimal(2000));
        failedSavingTarget.setCurrentAmount(new BigDecimal(500));
        failedSavingTarget.setDescription("Failed to save for a new laptop.");
        failedSavingTarget.setCreationDate(LocalDateTime.now().minusMonths(4));

        List<SavingTarget> createdInMonthTargets = List.of(createdSavingTarget);
        List<SavingTarget> incompleteActiveTargets = List.of(activeSavingTarget);
        List<SavingTarget> completedInMonthTargets = List.of(completedSavingTarget);
        List<SavingTarget> failedInMonthTargets = List.of(failedSavingTarget);

        // When
        try {
            emailService.sendMonthlyReport(
                    user.getEmail(),
                    "Your Monthly Financial Report",
                    "static/email_monthly_report.html",
                    user,
                    incomes,
                    expenses,
                    createdInMonthTargets,
                    incompleteActiveTargets,
                    completedInMonthTargets,
                    failedInMonthTargets,
                    createdInMonthSubs,
                    activeSubscriptions
            );
            emailSentWithSuccess = true;
        } catch (Exception e) {
            System.err.println("Error while sending email:");
            e.printStackTrace();
        }

        // Then
        assertTrue(emailSentWithSuccess, "The email was not sent successfully");
    }

    @Test
    void sendTestSubscriptionNotification() {
        // Given
        EmailConfig emailConfig = new EmailConfig("cosmeticsscraper@gmail.com", "qsiiavgzhjikydme");
        EmailService emailService = new EmailService(emailConfig);

        UserEntity user = new UserEntity();
        user.setEmail("marek.porebski01@gmail.com");

        Subscription subscription = new Subscription();
        subscription.setSubscriptionTitle("Amazon Prime");
        subscription.setAmount(new BigDecimal(12.99));
        subscription.setDescription("Monthly subscription for Amazon Prime");
        subscription.setCyclicalPaymentDate(25);

        boolean emailSentWithSuccess = false;

        // When
        try {
            emailService.sendSubscriptionNotification(user.getEmail(), "Subscription Payment Reminder",
                    "static/email_subscription_notification.html", subscription);
            emailSentWithSuccess = true;
        } catch (Exception e) {
            System.err.println("Error while sending subscription notification email:");
            e.printStackTrace();
        }

        // Then
        assertTrue(emailSentWithSuccess, "Subscription notification email was not sent successfully");
    }

    @Test
    void sendTestSavingGoalNotification() {
        // Given
        EmailConfig emailConfig = new EmailConfig("cosmeticsscraper@gmail.com", "qsiiavgzhjikydme");
        EmailService emailService = new EmailService(emailConfig);

        UserEntity user = new UserEntity();
        user.setEmail("marek.porebski01@gmail.com");

        SavingTarget savingTarget = new SavingTarget();
        savingTarget.setTargetTitle("New Laptop");
        savingTarget.setDescription("Saving for a high-end gaming laptop");
        savingTarget.setTargetAmount(new BigDecimal(2000));
        savingTarget.setCurrentAmount(new BigDecimal(800));
        savingTarget.setCyclicalPaymentAmount(new BigDecimal(100));

        boolean emailSentWithSuccess = false;

        // When
        try {
            emailService.sendSavingGoalNotification(user.getEmail(), "Saving Goal Reminder",
                    "static/email_saving_goal_notification.html", savingTarget);
            emailSentWithSuccess = true;
        } catch (Exception e) {
            System.err.println("Error while sending saving goal notification email:");
            e.printStackTrace();
        }

        // Then
        assertTrue(emailSentWithSuccess, "Saving goal notification email was not sent successfully");
    }


}

