package com.backend.tracker.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private final Map<String, List<LocalDateTime>> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String email) {
        List<LocalDateTime> userAttempts = attempts.getOrDefault(email, new ArrayList<>());
        LocalDateTime now = LocalDateTime.now();
        userAttempts.removeIf(time -> time.isBefore(now.minusMinutes(30)));
        attempts.put(email, userAttempts);
        return userAttempts.size() >= 5;
    }

    public void recordAttempt(String email) {
        attempts.computeIfAbsent(email, k -> new ArrayList<>()).add(LocalDateTime.now());
    }

    public void resetAttempts(String email) {
        attempts.remove(email);
    }

    public void loginSucceeded(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loginSucceeded'");
    }

    public void loginFailed(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loginFailed'");
    }
}


