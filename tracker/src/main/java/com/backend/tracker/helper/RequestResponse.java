package com.backend.tracker.helper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResponse {

    private String status;

    private String message;

    private Object data;
    
}
