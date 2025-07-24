package com.backend.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.tracker.entity.Budget;
import com.backend.tracker.entity.Users;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

     // Find exact budget for user on a specific day
    Optional<Budget> findByUserAndDayStartTime(Users user, Long dayStartTime);

    // Find the latest budget for the user (by dayStartTime descending)
    Optional<Budget> findTopByUserOrderByDayStartTimeDesc(Users user);

    
}
