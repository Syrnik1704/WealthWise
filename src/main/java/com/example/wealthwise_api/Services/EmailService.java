package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.Entity.SavingTarget;
import com.example.wealthwise_api.Entity.Subscription;
import com.example.wealthwise_api.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
            html = html.replace("SUBSCRIPTION_AMOUNT", subscription.getAmount().toString());
            html = html.replace("SUBSCRIPTION_DESCRIPTION", subscription.getDescription());
            emailConfig.sendEmail(userEmail, subject, html, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
