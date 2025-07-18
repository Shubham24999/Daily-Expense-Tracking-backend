
package com.backend.tracker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.tracker.entity.ExpenseDetails;

@Repository
public interface ExpenseDetailsRepository extends JpaRepository<ExpenseDetails, Long> {

    // Daily lookups (used elsewhere)
    List<ExpenseDetails> findByUserIdAndDayStartTime(Long userId, Long dayStartTime);

    List<ExpenseDetails> findByUserIdAndDayStartTimeOrderByExpenseCreatedTimeEpochDesc(Long userId, Long dayStartTime);

    // ---- Paging lookups ----
    Page<ExpenseDetails> findByUserId(Long userId, Pageable pageable);

    Page<ExpenseDetails> findByUserIdAndExpenseCreatedTimeEpochBetween(
            Long userId, Long fromEpoch, Long toEpoch, Pageable pageable);

    // ---- Aggregate totals ----
    @Query("select coalesce(sum(e.spentAmount),0) from ExpenseDetails e where e.userId = :userId")
    Double sumSpentByUserId(@Param("userId") Long userId);

    @Query("select coalesce(sum(e.spentAmount),0) from ExpenseDetails e " +
           "where e.userId = :userId and e.expenseCreatedTimeEpoch between :fromEpoch and :toEpoch")
    Double sumSpentByUserIdAndPeriod(@Param("userId") Long userId,
                                     @Param("fromEpoch") Long fromEpoch,
                                     @Param("toEpoch") Long toEpoch);
}



// package com.backend.tracker.repository;

// import java.util.List;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import com.backend.tracker.entity.ExpenseDetails;

// @Repository
// public interface ExpenseDetailsRepository extends JpaRepository<ExpenseDetails, Long> {

//     List<ExpenseDetails> findByUserIdAndDayStartTime(Long userId, Long dayStartTime);

//     List<ExpenseDetails> findByUserIdAndDayStartTimeOrderByExpenseCreatedTimeEpochDesc(Long userId, Long dayStartTime);

//     Page<ExpenseDetails> findByUserId(Long userId, Pageable pageable);

//     Page<ExpenseDetails> findByUserIdAndExpenseCreatedTimeEpochBetween(
//             Long userId, Long fromEpoch, Long toEpoch, Pageable pageable);

//     @Query("select coalesce(sum(e.spentAmount),0) from ExpenseDetails e where e.userId = :userId")
//     Double sumSpentByUserId(@Param("userId") Long userId);

//     @Query("select coalesce(sum(e.spentAmount),0) from ExpenseDetails e " +
//             "where e.userId = :userId and e.expenseCreatedTimeEpoch between :fromEpoch and :toEpoch")
//     Double sumSpentByUserIdAndPeriod(@Param("userId") Long userId,
//             @Param("fromEpoch") Long fromEpoch,
//             @Param("toEpoch") Long toEpoch);

// }
