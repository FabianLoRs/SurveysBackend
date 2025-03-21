package com.flopr.surveysbackend.models.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserLoginRequestModel {
    
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;
}

