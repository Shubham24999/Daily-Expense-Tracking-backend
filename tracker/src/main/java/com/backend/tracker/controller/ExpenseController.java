package com.backend.tracker.controller;

import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.BudgetAndExpenseDataModel;
import com.backend.tracker.service.ExpenseService;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin("*")
@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private static final Logger logger = LogManager.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getUserExpenseSummary(@PathVariable Long userId) {
        Map<String, Object> response = expenseService.getUserExpenseSummary(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @PostMapping("/create/budget")
    // public ResponseEntity<RequestResponse> createBudgetDetails(@RequestBody
    // BudgetAndExpenseDataModel requestData) {
    // RequestResponse response = expenseService.createBudget(requestData);
    // return new ResponseEntity<>(response, HttpStatus.CREATED);
    // }

    @PostMapping("/update/budget")
    public ResponseEntity<RequestResponse> updateBudgetDetails(@RequestBody BudgetAndExpenseDataModel requestData) {
        logger.info("Updating budget details for userId: " + requestData.getUserId());
        RequestResponse response = expenseService.updateBudget(requestData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<RequestResponse> addExpenseDetails(@RequestBody BudgetAndExpenseDataModel expenseDetails) {
        logger.info("Adding expense details for userId: " + expenseDetails.getUserId());
        RequestResponse response = expenseService.addExpenseDetails(expenseDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<RequestResponse> getExpenseDetails(@PathVariable Long userId) {
        logger.info("Fetching expense details for userId: " + userId);
        RequestResponse response = expenseService.getExpenseDetails(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/update/{expenseId}")
    public ResponseEntity<RequestResponse> updateExpenseDetails(@PathVariable Long expenseId,
            @RequestBody BudgetAndExpenseDataModel expenseData) {
        RequestResponse response = expenseService.updateExpenseDetails(expenseId, expenseData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/delete/{expenseId}")
    public ResponseEntity<RequestResponse> deleteExpenseDetails(@PathVariable Long expenseId) {
        RequestResponse response = expenseService.deleteExpenseDetails(expenseId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
