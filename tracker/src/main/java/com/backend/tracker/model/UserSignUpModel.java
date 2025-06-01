package com.backend.tracker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpModel {

    private String name;
    private String email;
    private String password;
    private Long phoneNumber;
  
}
