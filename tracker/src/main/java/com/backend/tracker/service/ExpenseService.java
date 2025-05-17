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

    public Map<String, Object> getUserExpenseSummary(Long userId) {
        Long todayEpochDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Map<String, Object> summary = new HashMap<>();

        Optional<Budget> budget = budgetRepository.findByUserIdAndDayStartTime(userId, todayEpochDay);

        double budgetAmount;
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

    public Map<String, Object> getDailySummary(Long userId, Long todayEpochDay) {
        Map<String, Object> summary = new HashMap<>();

        Optional<Budget> budget = budgetRepository.findByUserIdAndDayStartTime(userId, todayEpochDay);

        double budgetAmount;
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

}
