package com.backend.tracker.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.LoginRequestModel;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.repository.UsersRepository;

@Service
public class UserService {

    @Autowired
    private UsersRepository userRepository;

    public RequestResponse registerUser(UserSignUpModel model) {

        RequestResponse response = new RequestResponse();

        if (userRepository.existsByEmail(model.getEmail())) {
            response.setStatus("fail");
            response.setMessage("Email already exists!");
            return response;
        }

        Users user = new Users();
        user.setName(model.getName());
        user.setEmail(model.getEmail());
        user.setPassword(model.getPassword());
        user.setPhoneNumber(Long.valueOf(model.getPhoneNumber()));

        Users savedUser = userRepository.save(user);

        response.setStatus("Ok");
        response.setMessage("User registered successfully!");
        response.setData(savedUser);

        return response;

    }

    public RequestResponse loginUser(LoginRequestModel model) {

        RequestResponse response = new RequestResponse();

        Optional<Users> optionalUser = userRepository.findByEmail(model.getEmail());

        if (optionalUser.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("User not found!");
            return response;
        }

        Users user = optionalUser.get();
        if (!user.getPassword().equals(model.getPassword())) {
            response.setStatus("fail");
            response.setMessage("Invalid credentials!");
            return response;
        }

        response.setStatus("Ok");
        response.setMessage("Login successful!");
        response.setData(user);

        return response;

    }

}
