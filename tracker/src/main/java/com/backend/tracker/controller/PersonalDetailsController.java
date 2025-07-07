package com.backend.tracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.service.PersonalDetailsService;

@RestController
@RequestMapping("/api/profile")
public class PersonalDetailsController {

    @Autowired
    private PersonalDetailsService personalDetailsService;

    @GetMapping("/")
    public ResponseEntity<RequestResponse> getProfileDetails(Principal principal) {

        String email = principal.getName();

        RequestResponse response = personalDetailsService.getProfileDetails(email);

        if (response.getStatus().equalsIgnoreCase("OK")) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<RequestResponse> updateProfile(Principal principal,
            @RequestBody UserSignUpModel userData) {
        String email = principal.getName();
        RequestResponse response = personalDetailsService.updateProfileDetails(email, userData);

        if (response.getStatus().equalsIgnoreCase("OK")) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}
