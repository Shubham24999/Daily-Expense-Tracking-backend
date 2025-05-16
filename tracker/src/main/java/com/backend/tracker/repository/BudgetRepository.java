package com.backend.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.tracker.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

     // Find exact budget for user on a specific day
    Optional<Budget> findByUserIdAndDayStartTime(Long userId, Long dayStartTime);

    // Find the latest budget for the user (by dayStartTime descending)
    Optional<Budget> findTopByUserIdOrderByDayStartTimeDesc(Long userId);
    
}
