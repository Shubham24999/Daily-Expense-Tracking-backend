package com.backend.tracker.Keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DailyExpenseCompositeKey {
    
    @Column(name = "day_start_time")
    private Long dayStartTime;

    @Column(name = "user_id")
    private Long userId;
    
}
