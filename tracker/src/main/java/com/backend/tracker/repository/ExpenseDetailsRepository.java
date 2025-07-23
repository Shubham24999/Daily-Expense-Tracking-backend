
package com.backend.tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.tracker.entity.ExpenseDetails;

@Repository
public interface ExpenseDetailsRepository extends JpaRepository<ExpenseDetails, Long> {

    // ---- Daily lookups ----
    List<ExpenseDetails> findByUser_IdAndDayStartTime(Long userId, Long dayStartTime);

    List<ExpenseDetails> findByUser_IdAndDayStartTimeOrderByExpenseCreatedTimeEpochDesc(
            Long userId, Long dayStartTime);

    // ---- Paging lookups ----
    Page<ExpenseDetails> findByUser_Id(Long userId, Pageable pageable);

    Page<ExpenseDetails> findByUser_IdAndExpenseCreatedTimeEpochBetween(
            Long userId, Long fromEpoch, Long toEpoch, Pageable pageable);

    // ---- Aggregate totals ----
    @Query("SELECT COALESCE(SUM(e.spentAmount), 0) FROM ExpenseDetails e WHERE e.user.id = :userId")
    Double sumSpentByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(e.spentAmount), 0) FROM ExpenseDetails e " +
            "WHERE e.user.id = :userId AND e.expenseCreatedTimeEpoch BETWEEN :fromEpoch AND :toEpoch")
    Double sumSpentByUserIdAndPeriod(@Param("userId") Long userId,
            @Param("fromEpoch") Long fromEpoch,
            @Param("toEpoch") Long toEpoch);

    List<ExpenseDetails> findByUser_Id(Long userId);

    // ---- Custom date range query ----
    @Query("SELECT e FROM ExpenseDetails e " +
            "WHERE e.user.id = :userId AND e.expenseCreatedTimeEpoch BETWEEN :fromDate AND :toDate " +
            "ORDER BY e.expenseCreatedTimeEpoch DESC")
    List<ExpenseDetails> findExpensesByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("fromDate") Long fromDate,
            @Param("toDate") Long toDate);
}
