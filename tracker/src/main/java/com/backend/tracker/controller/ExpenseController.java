package com.backend.tracker.controller;

import com.backend.tracker.entity.ExpenseDetails;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.BudgetAndExpenseDataModel;
import com.backend.tracker.service.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create/budget")
    public ResponseEntity<RequestResponse> createBudgetDetails(@RequestBody BudgetAndExpenseDataModel requestData) {
        RequestResponse response = expenseService.createBudget(requestData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/add-details")
    public ResponseEntity<RequestResponse> addExpenseDetails(@RequestBody BudgetAndExpenseDataModel expenseDetails) {
        RequestResponse response = expenseService.addExpenseDetails(expenseDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

