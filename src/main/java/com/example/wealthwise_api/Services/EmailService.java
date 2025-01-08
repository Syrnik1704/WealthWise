package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.Entity.*;
import com.example.wealthwise_api.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailConfig emailConfig;

    public void sendEmail(String userEmail, String subject, String htmlTemplatePath) {
        try {
            ClassPathResource resource = new ClassPathResource(htmlTemplatePath);
            InputStream inputStream = resource.getInputStream();
            String html = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            emailConfig.sendEmail(userEmail, subject, html, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSavingGoalNotification(String userEmail, String subject, String htmlTemplatePath, SavingTarget savingTarget) {
        try {
            ClassPathResource resource = new ClassPathResource(htmlTemplatePath);
            InputStream inputStream = resource.getInputStream();
            String html = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            html = html.replace("SAVING_GOAL_TITLE", savingTarget.getTargetTitle());
            html = html.replace("SAVING_GOAL_DESCRIPTION", savingTarget.getDescription());
            html = html.replace("SAVING_AMOUNT", savingTarget.getCyclicalPaymentAmount().toString());
            html = html.replace("CURRENT_AMOUNT", savingTarget.getCurrentAmount().toString());
            html = html.replace("LEFT_AMOUNT", Integer.toString(savingTarget.getTargetAmount().intValue() - savingTarget.getCurrentAmount().intValue()));
            emailConfig.sendEmail(userEmail, subject, html, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSubscriptionNotification(String userEmail, String subject, String htmlTemplatePath, Subscription subscription) {
        try {
            ClassPathResource resource = new ClassPathResource(htmlTemplatePath);
            InputStream inputStream = resource.getInputStream();
            String html = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            html = html.replace("SUBSCRIPTION_TITLE", subscription.getSubscriptionTitle());
            html = html.replace("SUBSCRIPTION_AMOUNT", subscription.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
            html = html.replace("SUBSCRIPTION_DESCRIPTION", subscription.getDescription());
            emailConfig.sendEmail(userEmail, subject, html, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMonthlyReport(
            String userEmail,
            String subject,
            String htmlTemplatePath,
            UserEntity userEntity,
            List<Incomes> incomes,
            List<Expenses> expenses,
            List<SavingTarget> createdInMonthTargets,
            List<SavingTarget> incompleteActiveTargets,
            List<SavingTarget> completedInMonthTargets,
            List<SavingTarget> failedInMonthTargets,
            List<Subscription> createdInMonthSubs,
            List<Subscription> activeSubscriptions) {
        try {
            ClassPathResource resource = new ClassPathResource(htmlTemplatePath);
            InputStream inputStream = resource.getInputStream();
            String html = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

            // Replace placeholders with dynamic values
            html = html.replace("USER_NAME", userEntity.getName() + " " + userEntity.getSurname());

            // Replace incomes
            double totalIncomes = incomes.stream().mapToDouble(Incomes::getValue).sum();
            html = html.replace("TOTAL_INCOMES", String.valueOf(totalIncomes));
            String incomeDetails = incomes.stream()
                    .map(income -> income.getName() + " (" + income.getDescription() + "): " + income.getValue())
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("INCOME_DETAILS", incomeDetails.isEmpty() ? "No incomes recorded." : "<li>" + incomeDetails + "</li>");

            // Replace expenses
            double totalExpenses = expenses.stream().mapToDouble(Expenses::getAmount).sum();
            html = html.replace("TOTAL_EXPENSES", String.valueOf(totalExpenses));
            String expenseDetails = expenses.stream()
                    .map(expense -> expense.getName() + " (" + expense.getDescription() + "): " + expense.getAmount())
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("EXPENSE_DETAILS", expenseDetails.isEmpty() ? "No expenses recorded." : "<li>" + expenseDetails + "</li>");

            // Replace saving targets
            String createdTargetDetails = createdInMonthTargets.stream()
                    .map(target -> target.getTargetTitle() + " - Goal: " + target.getCurrentAmount() + "/" + target.getTargetAmount())
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("CREATED_TARGET_DETAILS", createdTargetDetails.isEmpty() ? "No new saving targets." : "<li>" + createdTargetDetails + "</li>");

            String activeTargetDetails = incompleteActiveTargets.stream()
                    .map(target -> target.getTargetTitle() + " - Progress: " + target.getCurrentAmount() + "/" + target.getTargetAmount())
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("ACTIVE_TARGET_DETAILS", activeTargetDetails.isEmpty() ? "No active saving targets." : "<li>" + activeTargetDetails + "</li>");

            String completedTargetDetails = completedInMonthTargets.stream()
                    .map(target -> target.getTargetTitle() + " - Completed: " + target.getTargetAmount())
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("COMPLETED_TARGET_DETAILS", completedTargetDetails.isEmpty() ? "No completed saving targets this month." : "<li>" + completedTargetDetails + "</li>");

            String failedTargetDetails = failedInMonthTargets.stream()
                    .map(target -> target.getTargetTitle() + " - Failed to reach: " + target.getTargetAmount())
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("FAILED_TARGET_DETAILS", failedTargetDetails.isEmpty() ? "No failed saving targets this month." : "<li>" + failedTargetDetails + "</li>");

            // Replace subscriptions
            String createdSubscriptionDetails = createdInMonthSubs.stream()
                    .map(sub -> sub.getSubscriptionTitle() + " - Amount: " + sub.getAmount() + " (Next Payment: Day " + sub.getCyclicalPaymentDate() + ")")
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("CREATED_SUBSCRIPTION_DETAILS", createdSubscriptionDetails.isEmpty() ? "No new subscriptions this month." : "<li>" + createdSubscriptionDetails + "</li>");

            String activeSubscriptionDetails = activeSubscriptions.stream()
                    .map(sub -> sub.getSubscriptionTitle() + " - Amount: " + sub.getAmount() + " (Next Payment: Day " + sub.getCyclicalPaymentDate() + ")")
                    .collect(Collectors.joining("</li><li>"));
            html = html.replace("ACTIVE_SUBSCRIPTION_DETAILS", activeSubscriptionDetails.isEmpty() ? "No active subscriptions." : "<li>" + activeSubscriptionDetails + "</li>");

            // Send email
            emailConfig.sendEmail(userEmail, subject, html, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
