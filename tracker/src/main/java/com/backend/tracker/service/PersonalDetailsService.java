package com.backend.tracker.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.repository.UsersRepository;

@Service
public class PersonalDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    public RequestResponse getProfileDetails(String email) {

        RequestResponse returnValue = new RequestResponse();

        try {
            Optional<Users> userData = usersRepository.findByEmail(email);

            if (userData.isPresent()) {

                UserSignUpModel userDetails = new UserSignUpModel();
                userDetails.setEmail(email);
                userDetails.setName(userData.get().getName());
                userDetails.setPhoneNumber(userData.get().getPhoneNumber());
                
                returnValue.setData(userDetails);

                returnValue.setMessage("User details are");
                returnValue.setStatus("OK");
            } else {
                returnValue.setMessage("User details not found.");
                returnValue.setStatus("NOT OK");
            }

        } catch (Exception e) {
            returnValue.setMessage("Error while getting user details");
            returnValue.setStatus("NOT OK");
        }

        return returnValue;
    }

    public RequestResponse updateProfileDetails(String email, UserSignUpModel userData) {
        RequestResponse returnValue = new RequestResponse();

        try {
            Optional<Users> userOptional = usersRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                Users existingUser = userOptional.get();
                boolean isUpdated = false;

                if (userData.getName() != null && !userData.getName().equals(existingUser.getName())) {
                    existingUser.setName(userData.getName());
                    isUpdated = true;
                }

                if (userData.getPhoneNumber() != null
                        && !userData.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                    existingUser.setPhoneNumber(userData.getPhoneNumber());
                    isUpdated = true;
                }

                // We skip password/email update unless explicitly allowed
                // You can add password encoding and update if needed

                if (isUpdated) {
                    usersRepository.save(existingUser);
                    returnValue.setStatus("OK");
                    returnValue.setMessage("Profile updated successfully");
                    returnValue.setData(existingUser);
                } else {
                    returnValue.setStatus("OK");
                    returnValue.setMessage("No changes detected in profile");
                    returnValue.setData(existingUser);
                }

            } else {
                returnValue.setStatus("NOT OK");
                returnValue.setMessage("User not found with email: " + email);
            }

        } catch (Exception e) {
            returnValue.setStatus("ERROR");
            returnValue.setMessage("Error while updating profile: " + e.getMessage());
        }

        return returnValue;
    }

}
