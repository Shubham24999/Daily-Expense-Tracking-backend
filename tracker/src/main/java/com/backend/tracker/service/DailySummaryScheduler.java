package com.backend.tracker.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.tracker.entity.Users;
import com.backend.tracker.repository.UsersRepository;

@Component
public class DailySummaryScheduler {

    private static final Logger logger = LogManager.getLogger(DailySummaryScheduler.class);

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private ExpenseService expenseService;

    @Scheduled(cron = "0 0 22 * * *") // Every day at 10 PM
    public void sendDailySummaries() {
        logger.info("--- Starting Daily Expense Summary at 10 PM ---");

        List<Users> users = userRepo.findAll();
        if (users.isEmpty()) {
            logger.warn("No users found for daily summary.");
            return;
        }

        Long todayEpochDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        for (Users user : users) {
            Map<String, Object> summary = expenseService.getDailySummary(user.getId(), todayEpochDay);

            double totalSpent = (double) summary.getOrDefault("totalSpent", 0.0);
            double budget = (double) summary.getOrDefault("budget", 0.0);
            double remaining = (double) summary.getOrDefault("remaining", 0.0);
            boolean exceeded = (boolean) summary.getOrDefault("exceeded", false);

            String status = exceeded
                    ? "❗ Alert: You have exceeded your budget!"
                    : "✅ You are within your budget.";

            logger.info("User: {}", user.getName());
            logger.info("Total Spent: ₹{}", totalSpent);
            logger.info("Budget: ₹{}", budget);
            logger.info("Remaining: ₹{}", remaining);
            logger.info(status);
            logger.info("---------------------------------------------");
        }

        logger.info("--- End of Daily Expense Summary ---");
    }
}
