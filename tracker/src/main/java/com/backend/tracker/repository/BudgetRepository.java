package com.backend.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.tracker.entity.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

     // Find exact budget for user on a specific day
    Optional<Budget> findByUser_IdAndDayStartTime(Long userId, Long dayStartTime);

    // Find the latest budget for the user (by dayStartTime descending)
    Optional<Budget> findTopByUser_IdOrderByDayStartTimeDesc(Long userId);

    
}
