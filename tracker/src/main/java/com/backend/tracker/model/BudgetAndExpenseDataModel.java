package com.backend.tracker.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetAndExpenseDataModel {

    private Long userId;
    private Long id;
    private String spentDetails;
    private Double budgetAmount;
    private Double spentAmount;
    private Double remainingAmount;
    private Long expenseId;
    private Long date;
    private Long expenseCreatedTime;
    private Integer numberOfExpenses;
    private Integer totalNumberOfExpenses;
    
}

