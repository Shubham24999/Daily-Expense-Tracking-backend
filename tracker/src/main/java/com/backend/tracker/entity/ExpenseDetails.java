package com.backend.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_details")
// , indexes = {
//         @Index(name = "idx_user_day", columnList = "userId, dayStartTime")
// })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "spent_amount")
    private Double spentAmount;

    @Column(name = "spent_details")
    private String spentDetails;

    @Column(name = "day_start_time")
    private Long dayStartTime;

    @Column(name = "expense_created_time_epoch")
    private Long expenseCreatedTimeEpoch;

    @Column(name = "expense_update_time_epoch")
    private Long expenseUpdateTimeEpoch;

}
