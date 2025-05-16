package com.backend.tracker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.tracker.entity.ExpenseDetails;

@Repository
public interface ExpenseDetailsRepository extends JpaRepository<ExpenseDetails, Long> {


    List<ExpenseDetails> findByUserIdAndDayStartTime(Long userId, Long dayStartTime);

}
