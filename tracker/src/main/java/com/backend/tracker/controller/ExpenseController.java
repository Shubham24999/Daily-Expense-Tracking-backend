package com.backend.tracker.controller;

import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.BudgetAndExpenseDataModel;
import com.backend.tracker.repository.UsersRepository;
import com.backend.tracker.service.ExpenseService;

import java.security.Principal;
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

    @Autowired
    private UsersRepository userRepository;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getUserExpenseSummary(Principal principal) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        
        Map<String, Object> response = expenseService.getUserExpenseSummary(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @PostMapping("/create/budget")
    // public ResponseEntity<RequestResponse> createBudgetDetails(@RequestBody
    // BudgetAndExpenseDataModel requestData) {
    // RequestResponse response = expenseService.createBudget(requestData);
    // return new ResponseEntity<>(response, HttpStatus.CREATED);
    // }

    @PostMapping("/update/budget")
    public ResponseEntity<RequestResponse> updateBudgetDetails(Principal principal,
            @RequestBody BudgetAndExpenseDataModel requestData) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        RequestResponse response = expenseService.updateBudget(user, requestData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<RequestResponse> addExpenseDetails(Principal principal,
            @RequestBody BudgetAndExpenseDataModel expenseDetails) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RequestResponse response = expenseService.addExpenseDetails(user, expenseDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<RequestResponse> getExpenseDetails(Principal principal) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RequestResponse response = expenseService.getExpenseDetails(user);
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
