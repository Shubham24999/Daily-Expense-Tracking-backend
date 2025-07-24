
package com.backend.tracker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.tracker.entity.ExpenseDetails;
import com.backend.tracker.entity.Users;

@Repository
public interface ExpenseDetailsRepository extends JpaRepository<ExpenseDetails, Long> {

        // --- Daily lookups ---
        List<ExpenseDetails> findByUserAndDayStartTime(Users user, Long dayStartTime);

        List<ExpenseDetails> findByUserAndDayStartTimeOrderByExpenseCreatedTimeEpochDesc(
                        Users user, Long dayStartTime);

        // --- Paging lookups ---
        Page<ExpenseDetails> findByUser(Users user, Pageable pageable);

        Page<ExpenseDetails> findByUserAndExpenseCreatedTimeEpochBetween(
                        Users user, Long fromEpoch, Long toEpoch, Pageable pageable);

        // --- Aggregate totals ---
        @Query("SELECT COALESCE(SUM(e.spentAmount), 0) FROM ExpenseDetails e WHERE e.user = :user")
        Double sumSpentByUser(@Param("user") Users user);

        @Query("SELECT COALESCE(SUM(e.spentAmount), 0) FROM ExpenseDetails e " +
                        "WHERE e.user = :user AND e.expenseCreatedTimeEpoch BETWEEN :fromEpoch AND :toEpoch")
        Double sumSpentByUserAndPeriod(@Param("user") Users user,
                        @Param("fromEpoch") Long fromEpoch,
                        @Param("toEpoch") Long toEpoch);

        List<ExpenseDetails> findByUser(Users user);

        // --- Custom date range query ---
        @Query("SELECT e FROM ExpenseDetails e " +
                        "WHERE e.user = :user AND e.expenseCreatedTimeEpoch BETWEEN :fromDate AND :toDate " +
                        "ORDER BY e.expenseCreatedTimeEpoch DESC")
        List<ExpenseDetails> findExpensesByUserAndDateRange(
                        @Param("user") Users user,
                        @Param("fromDate") Long fromDate,
                        @Param("toDate") Long toDate);

}
