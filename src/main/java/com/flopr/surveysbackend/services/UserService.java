package com.flopr.surveysbackend.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.flopr.surveysbackend.entities.UserEntity;
import com.flopr.surveysbackend.models.requests.UserRegisterRequestModel;

public interface UserService extends UserDetailsService {
    
    public UserDetails loadUserByUsername(String email);

    public UserEntity getUser(String email);

    public UserEntity createUser(UserRegisterRequestModel user);

}
