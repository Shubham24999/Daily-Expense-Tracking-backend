package com.backend.tracker.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseDetailsRepository expenseDetailsRepository;

    public Map<String, Object> getDailySummary(Long userId, Long todayEpochDay) {
        Map<String, Object> summary = new HashMap<>();

        // Fetching the user's expense record for today
        Optional<Budget> budget = budgetRepository.findByUserIdAndDayStartTime(userId, todayEpochDay);

        if (budget.isPresent()) {
            List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserIdAndDayStartTime(userId,
                    todayEpochDay);
            double totalSpent = 0;
            double budgetAmount = budget.get().getBudgetAmount();
            double remaining = budgetAmount > totalSpent ? (budgetAmount - totalSpent)
                    : (totalSpent - budgetAmount);
            boolean exceeded = totalSpent > budgetAmount;

            summary.put("totalSpent", totalSpent);
            summary.put("budget", budget);
            summary.put("remaining", remaining);
            summary.put("exceeded", exceeded);
        } else {

            Optional<Budget> userOldBudget = budgetRepository.findTopByUserIdOrderByDayStartTimeDesc(userId);

            if (userOldBudget.isPresent()) {
                List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserIdAndDayStartTime(userId,
                        todayEpochDay);
                double totalSpent = 0;
                double budgetAmount = userOldBudget.get().getBudgetAmount();
                double remaining = budgetAmount > totalSpent ? (budgetAmount - totalSpent)
                        : (totalSpent - budgetAmount);
                boolean exceeded = totalSpent > budgetAmount;

                summary.put("totalSpent", totalSpent);
                summary.put("budget", userOldBudget);
                summary.put("remaining", remaining);
                summary.put("exceeded", exceeded);

            } else {

                List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserIdAndDayStartTime(userId,
                        todayEpochDay);
                double totalSpent = 0;
                double budgetAmount = 1000L;
                double remaining = budgetAmount > totalSpent ? (budgetAmount - totalSpent)
                        : (totalSpent - budgetAmount);
                boolean exceeded = totalSpent > budgetAmount;

                summary.put("totalSpent", 0.0);
                summary.put("budget", null);
                summary.put("remaining", 0.0);
                summary.put("exceeded", false);

            }

        }

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
            // existingExpense.setSpentAmount(requestData.getSpentAmount());
            // existingExpense.setSpentDetails(requestData.getSpentDetails());
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
        Long userId=expenseDetails.getUserId();
        Long todayEpochSecond = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        RequestResponse response = new RequestResponse();
        if (expenseDetails.getSpentAmount() == null || expenseDetails.getSpentDetails() == null) {
            response.setStatus("sucess");
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

}
