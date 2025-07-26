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
import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.BudgetAndExpenseDataModel;
import com.backend.tracker.repository.ExpenseDetailsRepository;
import com.backend.tracker.repository.BudgetRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ExpenseService {

    private static final Logger logger = LogManager.getLogger(ExpenseService.class);

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseDetailsRepository expenseDetailsRepository;

    public Map<String, Object> getUserExpenseSummary(Users user) {

        Long todayEpochDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Map<String, Object> summary = new HashMap<>();

        Optional<Budget> budget = budgetRepository.findByUserAndDayStartTime(user, todayEpochDay);

        Double budgetAmount;
        if (budget.isPresent()) {
            budgetAmount = budget.get().getBudgetAmount();
        } else {
            Optional<Budget> userOldBudget = budgetRepository.findTopByUserOrderByDayStartTimeDesc(user);
            // budgetAmount = userOldBudget.map(Budget::getBudgetAmount).orElse(1000.0); //
            // fallback to 1000
            if (userOldBudget.isPresent()) {
                budgetAmount = userOldBudget.get().getBudgetAmount();
            } else {
                budgetAmount = 1000.0; // or any other default value you prefer
                logger.debug("No budget found for userId: {}. Using default budget amount: {}", user.getId(),
                        budgetAmount);
            }
        }

        List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserAndDayStartTime(user,
                todayEpochDay);
        if (expenseDetailsList.isEmpty()) {
            logger.debug("No expense details found for userId: {} on day: {}", user.getId(), todayEpochDay);
            return Map.of(
                    "userId", user.getId(),
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

        summary.put("userId", user.getId());
        summary.put("dayStartTime", todayEpochDay);
        summary.put("totalSpent", totalSpent);
        summary.put("budget", budgetAmount);
        summary.put("remaining", remaining);
        summary.put("exceeded", exceeded);

        return summary;
    }

    public Map<String, Object> getDailySummary(Users user, Long todayEpochDay) {
        Map<String, Object> summary = new HashMap<>();

        Optional<Budget> budget = budgetRepository.findByUserAndDayStartTime(user, todayEpochDay);

        Double budgetAmount;
        if (budget.isPresent()) {
            budgetAmount = budget.get().getBudgetAmount();
        } else {
            Optional<Budget> userOldBudget = budgetRepository.findTopByUserOrderByDayStartTimeDesc(user);
            budgetAmount = userOldBudget.map(Budget::getBudgetAmount).orElse(1000.0); // fallback to 1000
        }

        List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByUserAndDayStartTime(user,
                todayEpochDay);
        double totalSpent = expenseDetailsList.stream().mapToDouble(ExpenseDetails::getSpentAmount).sum();
        double remaining = budgetAmount > totalSpent ? (budgetAmount - totalSpent) : (totalSpent - budgetAmount);
        boolean exceeded = totalSpent > budgetAmount;

        summary.put("userId", user.getId());
        summary.put("dayStartTime", todayEpochDay);
        summary.put("totalSpent", totalSpent);
        summary.put("budget", budgetAmount);
        summary.put("remaining", remaining);
        summary.put("exceeded", exceeded);

        return summary;
    }

    public RequestResponse createBudget(Users user, BudgetAndExpenseDataModel requestData) {
        RequestResponse response = new RequestResponse();

        try {
            // Checking if the Expense for today already exists
            Long todayEpochSecond = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

            Optional<Budget> existingExpense = budgetRepository.findByUserAndDayStartTime(user, todayEpochSecond);
            Budget expenseDetails;
            if (existingExpense.isPresent()) {
                // Update existing Expense
                existingExpense.get().setBudgetAmount(requestData.getBudgetAmount());
                expenseDetails = budgetRepository.save(existingExpense.get());
            } else {
                // Create new Expense
                Budget newExpense = new Budget();
                newExpense.setUser(user);
                newExpense.setDayStartTime(todayEpochSecond);
                // newExpense.setSpentAmount(requestData.getSpentAmount());
                // newExpense.setSpentDetails(requestData.getSpentDetails());
                newExpense.setBudgetAmount(requestData.getBudgetAmount());
                expenseDetails = budgetRepository.save(newExpense);
            }

            response.setStatus("OK");
            response.setMessage("Budget saved successfully");
            response.setData(expenseDetails);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to creating user budget: " + e.getMessage());
            response.setData(null);
            logger.error("Error while creating user budget: {} {}", e.getMessage(), e);
        }
        return response;
    }

    public RequestResponse addExpenseDetails(Users user, BudgetAndExpenseDataModel expenseDetails) {
        RequestResponse response = new RequestResponse();
        try {

            Long todayEpochSecond = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

            if (expenseDetails.getSpentAmount() == null || expenseDetails.getSpentDetails() == null) {
                response.setStatus("Not Sucess");
                response.setMessage("Please provide all the required spent details");
                response.setData(null);
                return response;
            }
            ExpenseDetails newExpenseDetails = new ExpenseDetails();

            newExpenseDetails.setUser(user);
            newExpenseDetails.setSpentAmount(expenseDetails.getSpentAmount());
            newExpenseDetails.setSpentDetails(
                    StringUtils.hasText(expenseDetails.getSpentDetails()) ? expenseDetails.getSpentDetails()
                            : "Expense");
            newExpenseDetails.setDayStartTime(todayEpochSecond);
            newExpenseDetails.setExpenseCreatedTimeEpoch(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            response.setData(expenseDetailsRepository.save(newExpenseDetails));

            response.setStatus("OK");
            response.setMessage("Expense details saved successfully");
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to add expense details: " + e.getMessage());
            response.setData(null);
            logger.error("Error while adding expense details: {} {}", e.getMessage(), e);
        }
        return response;

    }

    public RequestResponse getExpenseDetails(Users user) {
        RequestResponse response = new RequestResponse();
        try {
            List<BudgetAndExpenseDataModel> expenseDataList = new ArrayList<>();
            Long dayStartTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

            List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository
                    .findByUserAndDayStartTimeOrderByExpenseCreatedTimeEpochDesc(user, dayStartTime);
            Long number = 1L;

            for (ExpenseDetails expenseDetails : expenseDetailsList) {
                BudgetAndExpenseDataModel expenseData = new BudgetAndExpenseDataModel();
                expenseData.setId(number++);
                expenseData.setExpenseId(expenseDetails.getId());
                expenseData.setSpentAmount(expenseDetails.getSpentAmount());
                expenseData.setSpentDetails(expenseDetails.getSpentDetails());
                expenseData.setExpenseCreatedTime(expenseDetails.getExpenseCreatedTimeEpoch());

                expenseData.setDate(expenseDetails.getDayStartTime());
                expenseData.setUserId(expenseDetails.getUser().getId());
                expenseDataList.add(expenseData);
            }
            // expenseData.setTotalNumberOfExpenses(expenseDetailsList.size());
            // expenseData.setNumberOfExpenses();

            if (expenseDetailsList.isEmpty()) {
                response.setStatus("OK");
                response.setMessage("No Expense details found for the given user and date");
                response.setData(null);
            } else {
                response.setStatus("OK");
                response.setMessage("Expense details fetched successfully");
                response.setData(expenseDataList);
            }
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to get expense details: " + e.getMessage());
            response.setData(null);
            logger.error("Error while getting expense details: {} {}", e.getMessage(), e);
        }
        return response;
    }

    public RequestResponse updateBudget(Users user, BudgetAndExpenseDataModel requestData) {

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
            Optional<Budget> existingBudget = budgetRepository.findByUserAndDayStartTime(user, todayEpochSecond);

            if (existingBudget.isPresent()) {
                // Update existing Budget
                existingBudget.get().setBudgetAmount(requestData.getBudgetAmount());
                budgetRepository.save(existingBudget.get());
                response.setStatus("OK");
                response.setMessage("Budget updated successfully");
                response.setData(existingBudget.get());
            } else {
                // Create new Budget
                Budget newBudget = new Budget();
                newBudget.setUser(user);
                newBudget.setDayStartTime(todayEpochSecond);
                newBudget.setBudgetAmount(requestData.getBudgetAmount());
                budgetRepository.save(newBudget);
                response.setStatus("OK");
                response.setMessage("New budget created successfully");
                response.setData(newBudget);
            }
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to update or create budget: " + e.getMessage());
            logger.error("Error updating or creating budget: {} {}", e.getMessage(), e);
        }

        return response;
    }

    public RequestResponse updateExpenseDetails(Long expenseId, BudgetAndExpenseDataModel expenseData) {

        RequestResponse returnValue = new RequestResponse();

        try {

            Optional<ExpenseDetails> expenseDetails = expenseDetailsRepository.findById(expenseId);
            if (expenseDetails.isPresent()) {
                ExpenseDetails existingExpenseDetails = expenseDetails.get();
                boolean isUpdated = false;

                if (StringUtils.hasText(expenseData.getSpentDetails())
                        && !expenseData.getSpentDetails().equalsIgnoreCase(existingExpenseDetails.getSpentDetails())) {
                    existingExpenseDetails.setSpentDetails(expenseData.getSpentDetails());
                    isUpdated = true;

                }
                if (expenseData.getSpentAmount() > 0 &&
                        Double.compare(expenseData.getSpentAmount(), existingExpenseDetails.getSpentAmount()) != 0) {
                    existingExpenseDetails
                            .setExpenseUpdateTimeEpoch(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
                    existingExpenseDetails.setSpentAmount(expenseData.getSpentAmount());
                    isUpdated = true;
                }

                if (isUpdated) {
                    returnValue.setData(expenseDetailsRepository.save(existingExpenseDetails));
                    returnValue.setMessage("Updated expense details for: " + expenseId);
                } else {
                    returnValue.setMessage("No Changes In expenses for: {}" + expenseId);
                }
                returnValue.setStatus("OK");

            } else {
                returnValue.setMessage("No Expenses find for: {}" + expenseId);
                returnValue.setStatus("NOT_FOUND");
            }

        } catch (Exception e) {
            returnValue.setStatus("error");
            returnValue.setMessage("Failed to update expense details: " + e.getMessage() + " for: " + expenseId);
            logger.error("Error while update expense details: {} {}", e.getMessage(), e);
        }

        return returnValue;
    }

    public RequestResponse deleteExpenseDetails(Long expenseId) {

        RequestResponse returnValue = new RequestResponse();

        if (expenseId == null || expenseId <= 0) {
            returnValue.setStatus("error");
            returnValue.setMessage("Invalid expense ID provided");
            return returnValue;
        }

        try {
            Optional<ExpenseDetails> expenseDetails = expenseDetailsRepository.findById(expenseId);
            if (expenseDetails.isPresent()) {
                expenseDetailsRepository.deleteById(expenseId);
                returnValue.setData(expenseDetails);
                returnValue.setMessage("Expense deleted : " + expenseId);
                returnValue.setStatus("OK");

            } else {
                returnValue.setMessage("No Expenses find for: {}" + expenseId);
                returnValue.setStatus("NOT_FOUND");
            }
            logger.debug("Expense deleted successfully: {}", expenseId);

        } catch (Exception e) {
            returnValue.setStatus("error");
            returnValue.setMessage("Failed to delete expense details: " + e.getMessage() + " for: " + expenseId);
            logger.error("Error while deleting expense details: {} {}", e.getMessage(), e);
        }

        return returnValue;
    }

    public RequestResponse getExpenseDetails(Users user, int page, int size, String fromDate, String toDate) {
        RequestResponse response = new RequestResponse();
        try {
            Long fromEpoch = null;
            Long toEpoch = null;

            if (fromDate != null && toDate != null) {
                LocalDate from = LocalDate.parse(fromDate);
                LocalDate to = LocalDate.parse(toDate);
                fromEpoch = from.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                toEpoch = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond(); // inclusive range
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseCreatedTimeEpoch"));
            Page<ExpenseDetails> expensePage;

            if (fromEpoch != null && toEpoch != null) {
                expensePage = expenseDetailsRepository
                        .findByUserAndExpenseCreatedTimeEpochBetween(user, fromEpoch, toEpoch, pageable);
            } else {
                expensePage = expenseDetailsRepository.findByUser(user, pageable);
            }

            Double totalSpent = 0.0;
            if (fromEpoch != null && toEpoch != null) {
                totalSpent = expenseDetailsRepository
                        .sumSpentByUserAndPeriod(user, fromEpoch, toEpoch);
            } else {
                totalSpent = expenseDetailsRepository.sumSpentByUser(user);
            }
            if (totalSpent == null) {
                totalSpent = 0.0;
            }

            List<BudgetAndExpenseDataModel> expenseDataList = new ArrayList<>();
            Long number = 1L;
            for (ExpenseDetails expenseDetails : expensePage.getContent()) {
                BudgetAndExpenseDataModel expenseData = new BudgetAndExpenseDataModel();
                expenseData.setId(number++);
                expenseData.setExpenseId(expenseDetails.getId());
                expenseData.setSpentAmount(expenseDetails.getSpentAmount());
                expenseData.setSpentDetails(expenseDetails.getSpentDetails());
                expenseData.setExpenseCreatedTime(expenseDetails.getExpenseCreatedTimeEpoch());
                expenseData.setDate(expenseDetails.getDayStartTime());
                expenseData.setUserId(expenseDetails.getUser().getId());
                expenseDataList.add(expenseData);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("expenses", expenseDataList);
            result.put("currentPage", expensePage.getNumber());
            result.put("totalPages", expensePage.getTotalPages());
            result.put("totalItems", expensePage.getTotalElements());
            result.put("totalSpent", totalSpent);

            response.setStatus("OK");
            response.setMessage(
                    expenseDataList.isEmpty() ? "No Expense details found" : "Expense details fetched successfully");
            response.setData(result);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Failed to get expense details: " + e.getMessage());
            response.setData(null);
            logger.error("Error while getting expense details: {} {}", e.getMessage(), e);
        }
        return response;
    }

    public List<ExpenseDetails> getExpensesByDateRange(Users user, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("From date and to date must not be null");
        }

        Long fromEpoch = fromDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        Long toEpoch = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond(); // inclusive

        return expenseDetailsRepository.findExpensesByUserAndDateRange(user, fromEpoch, toEpoch);
    }

}
