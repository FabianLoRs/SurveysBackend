package com.flopr.surveysbackend.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.flopr.surveysbackend.annotations.UniqueEmail;
import com.flopr.surveysbackend.entities.UserEntity;
import com.flopr.surveysbackend.repositories.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        UserEntity user = userRepository.findByEmail(value);

        if (user == null) {

            return true;
            
        }

        return false;
    }
    
}
