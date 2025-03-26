package com.flopr.surveysbackend.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.flopr.surveysbackend.validators.ValueOfEnumValidator;

@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueOfEnum {

    Class<? extends Enum<?>> enumClass();

    String message() default "{surveysbackend.constraints.enum.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
}
