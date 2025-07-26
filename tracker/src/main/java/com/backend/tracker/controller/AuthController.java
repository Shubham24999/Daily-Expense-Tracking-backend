package com.backend.tracker.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.LoginRequestModel;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.service.UserService;

// @CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<RequestResponse> registerUser(@RequestBody UserSignUpModel model) {

        RequestResponse response = userService.registerUser(model);
        logger.debug("User registration status is : {}", response.getStatus());

        if (response.getStatus().equalsIgnoreCase("OK")) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<RequestResponse> loginUser(@RequestBody LoginRequestModel model) {
        RequestResponse response = userService.loginUser(model);
        return ResponseEntity.ok(response);

    }

    // create api for update password..

}
