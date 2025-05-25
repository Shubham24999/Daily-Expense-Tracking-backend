package com.backend.tracker.controller;

import com.backend.tracker.entity.ExpenseDetails;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.BudgetAndExpenseDataModel;
import com.backend.tracker.service.ExpenseService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getUserExpenseSummary(@PathVariable Long userId) {
        Map<String, Object> response = expenseService.getUserExpenseSummary(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @PostMapping("/create/budget")
    // public ResponseEntity<RequestResponse> createBudgetDetails(@RequestBody BudgetAndExpenseDataModel requestData) {
    //     RequestResponse response = expenseService.createBudget(requestData);
    //     return new ResponseEntity<>(response, HttpStatus.CREATED);
    // }

    @PostMapping("/update/budget")
    public ResponseEntity<RequestResponse> updateBudgetDetails(@RequestBody BudgetAndExpenseDataModel requestData) {
        RequestResponse response = expenseService.updateBudget(requestData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<RequestResponse> addExpenseDetails(@RequestBody BudgetAndExpenseDataModel expenseDetails) {
        RequestResponse response = expenseService.addExpenseDetails(expenseDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<RequestResponse> getExpenseDetails(@PathVariable Long userId) {
        RequestResponse response = expenseService.getExpenseDetails(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
