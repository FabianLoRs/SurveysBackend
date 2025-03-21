package com.flopr.surveysbackend.models.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.flopr.surveysbackend.annotations.UniqueEmail;

import lombok.Data;

@Data
 public class UserRegisterRequestModel {

    @NotEmpty
    private String name;
    
    @NotEmpty
    @Email
    @UniqueEmail
    private String email;

    @NotEmpty
    @Size(min = 8, max = 40)
    private String password;
}
