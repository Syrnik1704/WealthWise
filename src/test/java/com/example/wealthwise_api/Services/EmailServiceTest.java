package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.config.EmailConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailServiceTest {

    @Test
    void sendTestEmailTest() {
        // Given
        EmailConfig emailConfig = new EmailConfig("cosmeticsscraper@gmail.com", "qsiiavgzhjikydme");
        EmailService emailService = new EmailService(emailConfig);
        UserEntity user = new UserEntity();
        user.setEmail("marek.porebski01@gmail.com");
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
}

