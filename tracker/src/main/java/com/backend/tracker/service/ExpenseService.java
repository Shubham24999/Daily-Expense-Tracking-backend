package com.backend.tracker.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.backend.tracker.entity.Budget;
import com.backend.tracker.entity.ExpenseDetails;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.BudgetAndExpenseDataModel;
import com.backend.tracker.repository.ExpenseDetailsRepository;
import com.backend.tracker.repository.BudgetRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private static final Logger logger = LogManager.getLogger(ExpenseService.class);

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseDetailsRepository expenseDetailsRepository;

    public Map<String, Object> getUserExpenseSummary(Long userId) {
        Long todayEpochDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Map<String, Object> summary = new HashMap<>();

        Optional<Budget> budget = budgetRepository.findByUserIdAndDayStartTime(userId, todayEpochDay);

        Double budgetAmount;
        if (budget.isPresent()) {
            budgetAmount = budget.get().getBudgetAmount();
        } else {
            Optional<Budget> userOldBudget = budgetRepository.findTopByUserIdOrderByDayStartTimeDesc(userId);
            // budgetAmount = userOldBudget.map(Budget::getBudgetAmount).orElse(1000.0); //
            // fallback to 1000
            if (userOldBudget.isPresent()) {
                budgetAmount = userOldBudget.get().getBudgetAmount();
            } else {
                budgetAmount = 1000.0; // or any other default value you prefer
                logger.info("No budget found for userId: {}. Using default budget amount: {}", userId, budgetAmount);
            }
        }

        List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserIdAndDayStartTime(userId,
                todayEpochDay);
        if (expenseDetailsList.isEmpty()) {
            logger.info("No expense details found for userId: {} on day: {}", userId, todayEpochDay);
            return Map.of(
                    "userId", userId,
                    "dayStartTime", todayEpochDay,
                    "totalSpent", 0.0,
                    "budget", budgetAmount,
                    "remaining", budgetAmount,
                    "exceeded", false);
        }

        double totalSpent = expenseDetailsList.stream()
                .mapToDouble(ExpenseDetails::getSpentAmount)
                .sum();
        // =
        // expenseDetailsList.stream().mapToDouble(ExpenseDetails::getSpentAmount).sum();
        double remaining = budgetAmount > totalSpent ? (budgetAmount - totalSpent) : (totalSpent - budgetAmount);
        boolean exceeded = totalSpent > budgetAmount;

        summary.put("userId", userId);
        summary.put("dayStartTime", todayEpochDay);
        summary.put("totalSpent", totalSpent);
        summary.put("budget", budgetAmount);
        summary.put("remaining", remaining);
        summary.put("exceeded", exceeded);

        return summary;
    }

    public Map<String, Object> getDailySummary(Long userId, Long todayEpochDay) {
        Map<String, Object> summary = new HashMap<>();

        Optional<Budget> budget = budgetRepository.findByUserIdAndDayStartTime(userId, todayEpochDay);

        Double budgetAmount;
        if (budget.isPresent()) {
            budgetAmount = budget.get().getBudgetAmount();
        } else {
            Optional<Budget> userOldBudget = budgetRepository.findTopByUserIdOrderByDayStartTimeDesc(userId);
            budgetAmount = userOldBudget.map(Budget::getBudgetAmount).orElse(1000.0); // fallback to 1000
        }

        List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserIdAndDayStartTime(userId,
                todayEpochDay);
        double totalSpent = expenseDetailsList.stream().mapToDouble(ExpenseDetails::getSpentAmount).sum();
        double remaining = budgetAmount > totalSpent ? (budgetAmount - totalSpent) : (totalSpent - budgetAmount);
        boolean exceeded = totalSpent > budgetAmount;

        summary.put("userId", userId);
        summary.put("dayStartTime", todayEpochDay);
        summary.put("totalSpent", totalSpent);
        summary.put("budget", budgetAmount);
        summary.put("remaining", remaining);
        summary.put("exceeded", exceeded);

        return summary;
    }

    public RequestResponse createBudget(BudgetAndExpenseDataModel requestData) {
        // Checking if the Expense for today already exists
        Long todayEpochSecond = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Optional<Budget> existingExpense = budgetRepository.findByUserIdAndDayStartTime(requestData.getUserId(),
                todayEpochSecond);
        Budget expenseDeatils;
        if (existingExpense.isPresent()) {
            // Update existing Expense
            existingExpense.get().setBudgetAmount(requestData.getBudgetAmount());
            expenseDeatils = budgetRepository.save(existingExpense.get());
        } else {
            // Create new Expense
            Budget newExpense = new Budget();
            newExpense.setUserId(requestData.getUserId());
            newExpense.setDayStartTime(todayEpochSecond);
            // newExpense.setSpentAmount(requestData.getSpentAmount());
            // newExpense.setSpentDetails(requestData.getSpentDetails());
            newExpense.setBudgetAmount(requestData.getBudgetAmount());
            expenseDeatils = budgetRepository.save(newExpense);
        }

        RequestResponse response = new RequestResponse();
        response.setStatus("success");
        response.setMessage("Budget saved successfully");
        response.setData(expenseDeatils);
        return response;
    }

    public RequestResponse addExpenseDetails(BudgetAndExpenseDataModel expenseDetails) {
        // will get userdetails from Authentication or Principal
        Long userId = expenseDetails.getUserId();
        Long todayEpochSecond = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        RequestResponse response = new RequestResponse();
        if (expenseDetails.getSpentAmount() == null || expenseDetails.getSpentDetails() == null) {
            response.setStatus("Not Sucess");
            response.setMessage("Please provide all the required spent details");
            response.setData(null);
            return response;
        }
        ExpenseDetails newExpenseDetails = new ExpenseDetails();

        newExpenseDetails.setUserId(userId);
        newExpenseDetails.setSpentAmount(expenseDetails.getSpentAmount());
        newExpenseDetails.setSpentDetails(expenseDetails.getSpentDetails());
        newExpenseDetails.setDayStartTime(todayEpochSecond);
        newExpenseDetails.setExpenseCreatedTimeEpoch(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        response.setData(expenseDetailsRepository.save(newExpenseDetails));

        response.setStatus("success");
        response.setMessage("Expense details saved successfully");
        return response;

    }

    public RequestResponse getExpenseDetails(Long userId) {
        RequestResponse response = new RequestResponse();
        List<ExpenseDetails> expenseDataList = new ArrayList<>();
        Long dayStartTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository
                .findByUserIdAndDayStartTimeOrderByExpenseCreatedTimeEpochDesc(userId, dayStartTime);

        for (ExpenseDetails expenseDetails : expenseDetailsList) {
            ExpenseDetails expenseData = new ExpenseDetails();
            expenseData.setId(expenseDetails.getId());
            expenseData.setSpentAmount(expenseDetails.getSpentAmount());
            expenseData.setSpentDetails(expenseDetails.getSpentDetails());
            expenseData.setExpenseCreatedTimeEpoch(expenseDetails.getExpenseCreatedTimeEpoch());
            expenseData.setDayStartTime(expenseDetails.getDayStartTime());
            expenseDataList.add(expenseData);
        }

        if (expenseDetailsList.isEmpty()) {
            response.setStatus("success");
            response.setMessage("No Expense details found for the given user and date");
            response.setData(null);
        } else {
            response.setStatus("success");
            response.setMessage("Expense details fetched successfully");
            response.setData(expenseDataList);
        }
        return response;
    }

    public RequestResponse updateBudget(BudgetAndExpenseDataModel requestData) {

        RequestResponse response = new RequestResponse();
        // handle empty budget amount
        if (requestData.getBudgetAmount() == null || requestData.getBudgetAmount() <= 0) {
            response.setStatus("error");
            response.setMessage("Invalid budget amount provided");
            response.setData(null);
            return response;
        }

        Long todayEpochSecond = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        try {
            Optional<Budget> existingBudget = budgetRepository.findByUserIdAndDayStartTime(requestData.getUserId(),
                    todayEpochSecond);

            if (existingBudget.isPresent()) {
                // Update existing Budget
                existingBudget.get().setBudgetAmount(requestData.getBudgetAmount());
                budgetRepository.save(existingBudget.get());
                response.setStatus("success");
                response.setMessage("Budget updated successfully");
                response.setData(existingBudget.get());
            } else {
                // Create new Budget
                Budget newBudget = new Budget();
                newBudget.setUserId(requestData.getUserId());
                newBudget.setDayStartTime(todayEpochSecond);
                newBudget.setBudgetAmount(requestData.getBudgetAmount());
                budgetRepository.save(newBudget);
                response.setStatus("success");
                response.setMessage("New budget created successfully");
                response.setData(newBudget);
            }
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to update or create budget: " + e.getMessage());
            response.setData(null);
            logger.info("Error updating or creating budget: {} {}", e.getMessage(), e);
        }

        return response;
    }

}
