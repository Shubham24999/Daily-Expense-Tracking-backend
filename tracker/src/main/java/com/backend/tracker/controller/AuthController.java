package com.backend.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.tracker.helper.SignUpLogInResponse;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.LoginRequestModel;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpLogInResponse> registerUser(@RequestBody UserSignUpModel model) {
        SignUpLogInResponse response = userService.registerUser(model);
        return new ResponseEntity<>(response,  
                response.getStatus().equals("Ok") ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<SignUpLogInResponse> loginUser(@RequestBody LoginRequestModel model) {
        SignUpLogInResponse response = userService.loginUser(model);
        return new ResponseEntity<>(response,
                response.getStatus().equals("Ok") ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }
}
