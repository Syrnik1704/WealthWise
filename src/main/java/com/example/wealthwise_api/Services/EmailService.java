package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.Entity.UserEntity;
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

}
