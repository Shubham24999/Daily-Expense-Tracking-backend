package com.backend.tracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.core.Authentication;
import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.SignUpLogInResponse;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.LoginRequestModel;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.repository.UsersRepository;

@Service
public class UserService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private JwtService jwtUtilsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public SignUpLogInResponse registerUser(UserSignUpModel model) {

        SignUpLogInResponse response = new SignUpLogInResponse();

        if (StringUtils.hasText(model.getEmail()) && StringUtils.hasText(model.getPassword())) {

            if (userRepository.existsByEmail(model.getEmail())) {
                response.setStatus("fail");
                response.setMessage("Email already exists!");
                return response;
            }

            String[] requiredData = model.getEmail().split("@");

            if (requiredData[1].equals("gmail.com") || requiredData[1].equals("yahoo.com")
                    || requiredData[1].equals("outlook.com")) {

                Users user = new Users();
                user.setName(StringUtils.hasText(model.getName()) ? model.getName() : requiredData[0]);
                user.setEmail(model.getEmail());
                user.setPassword(passwordEncoder.encode(model.getPassword()));

                user.setPhoneNumber((model.getPhoneNumber() != null) ? model.getPhoneNumber() : 0L);

                Users savedUser = userRepository.save(user);

                String token = jwtUtilsService.generateToken(model.getEmail());
                response.setToken(token);

                response.setStatus("Ok");
                response.setMessage("User registered successfully!");
                response.setData(savedUser);

                return response;

            } else {
                response.setStatus("fail");
                response.setMessage("Please Provide Email and Reuired Data in Correct Format!");
                return response;
            }
        } else {
            response.setStatus("fail");
            response.setMessage("Please Provide Email and password Data!");
            return response;
        }

    }

    public SignUpLogInResponse loginUser(LoginRequestModel model) {

        SignUpLogInResponse response = new SignUpLogInResponse();

        String[] requiredData = model.getEmail().split("@");

        if (requiredData[1].equals("gmail.com") || requiredData[1].equals("yahoo.com")
                || requiredData[1].equals("outlook.com")) {

            Optional<Users> optionalUser = userRepository.findByEmail(model.getEmail());

            if (optionalUser.isEmpty()) {
                response.setStatus("fail");
                response.setMessage("User not found!");
                return response;
            }

            Users user = optionalUser.get();
            // this should be-- authenticationManager where we have declare with name like
            // in Security Config
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), model.getPassword()));

            if (authentication.isAuthenticated()) {

                String token = jwtUtilsService.generateToken(model.getEmail());
                response.setToken(token);
                response.setStatus("OK");
                response.setMessage("User logged in successfully");
            } else {
                response.setStatus("Not OK");
                response.setMessage("Invalid credentials");
            }

        } else {
            response.setStatus("fail");
            response.setMessage("Please Provide Email in Correct Format!");
            return response;
        }

        return response;

    }

}
