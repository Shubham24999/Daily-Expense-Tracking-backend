package com.backend.tracker.helper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpLogInResponse {
    private String status;
    private String message;
    private String token;

    private Object data;
    

}

