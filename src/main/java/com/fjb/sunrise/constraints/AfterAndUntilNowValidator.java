package com.fjb.sunrise.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AfterAndUntilNowValidator implements ConstraintValidator<AfterAndUntilNow, LocalDateTime> {

    private LocalDateTime input;

    @Override
    public void initialize(AfterAndUntilNow constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        input = LocalDateTime.parse(constraintAnnotation.value(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = true;
        if(dateTime != null) {
            if(!(dateTime.isAfter(input)&&dateTime.isBefore(LocalDateTime.now()))){
                valid = false;
            }
        }
        return valid;
    }
}
