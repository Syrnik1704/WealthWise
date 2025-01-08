package com.example.wealthwise_api.Scheduler;

import com.example.wealthwise_api.Entity.*;
import com.example.wealthwise_api.Repository.*;
import com.example.wealthwise_api.Services.EmailService;
import jakarta.jws.soap.SOAPBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MonthlyReportScheduler {

    private final UserEntityRepository userEntityRepository;
    private final SavingTargetRepository savingTargetRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final IncomesRepository incomesRepository;
    private final ExpensesRepository expensesRepository;
    Logger logger = LoggerFactory.getLogger(MonthlyReportScheduler.class);
    private final EmailService emailService;

    public MonthlyReportScheduler(
            UserEntityRepository userEntityRepository, SavingTargetRepository savingTargetRepository,
            EmailService emailService, SubscriptionRepository subscriptionRepository, IncomesRepository incomesRepository, ExpensesRepository expensesRepository) {
        this.savingTargetRepository = savingTargetRepository;
        this.emailService = emailService;
        this.userEntityRepository = userEntityRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.incomesRepository = incomesRepository;
        this.expensesRepository = expensesRepository;
    }

    @Scheduled(cron = "0 0 10 L * ?")
    public void sendMonthlyReport() {
        List<UserEntity> users = userEntityRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        for (UserEntity user : users) {
            if (user.getIsActive()) {
                // saving targets
                List<SavingTarget> createdInMonthTargets = savingTargetRepository.findCreatedInMonth(month, year, user.getIdUser());
                List<SavingTarget> incompleteActiveTargets = savingTargetRepository.findIncompleteActiveTargets(month, year, user.getIdUser());
                List<SavingTarget> completedInMonthTargets = savingTargetRepository.findCompletedInMonth(month, year, user.getIdUser());
                List<SavingTarget> failedInMonthTargets = savingTargetRepository.findFailedTargetsInMonth(month, year, user.getIdUser());

                // subscriptions
                List<Subscription> createdInMonthSubs = subscriptionRepository.findCreatedInMonth(month, year, user.getIdUser());
                List<Subscription> activeSubscriptions = subscriptionRepository.findActiveSubscriptions(user.getIdUser());

                // incomes
                List<Incomes> createdInMonthIncomes = incomesRepository.findCreatedInMonth(month, year, user.getIdUser());

                // expenses
                List<Expenses> createdInMonthExpenses = expensesRepository.findCreatedInMonth(month, year, user.getIdUser());

                sendMonthlyReport(
                        user,
                        createdInMonthIncomes,
                        createdInMonthExpenses,
                        createdInMonthTargets,
                        incompleteActiveTargets,
                        completedInMonthTargets,
                        failedInMonthTargets,
                        createdInMonthSubs,
                        activeSubscriptions
                );
            }
        }
    }

    private void sendMonthlyReport(
            UserEntity user,
            List<Incomes> createdInMonthIncomes,
            List<Expenses> createdInMonthExpenses,
            List<SavingTarget> createdInMonthTargets,
            List<SavingTarget> incompleteActiveTargets,
            List<SavingTarget> completedInMonthTargets,
            List<SavingTarget> failedInMonthTargets,
            List<Subscription> createdInMonthSubs,
            List<Subscription> activeSubscriptions
    ) {
        try {
            emailService.sendMonthlyReport(
                    user.getEmail(),
                    "Your Monthly Financial Report",
                    "templates/monthly_report_template.html",
                    user,
                    createdInMonthIncomes,
                    createdInMonthExpenses,
                    createdInMonthTargets,
                    incompleteActiveTargets,
                    completedInMonthTargets,
                    failedInMonthTargets,
                    createdInMonthSubs,
                    activeSubscriptions
            );
            logger.info("Notification about saving goal sent to " + user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send notification about saving goal to " + user.getEmail() + ": " + e.getMessage());
        }
    }
}
