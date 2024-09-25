package com.fjb.sunrise.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StringMustBeDigitWithFractionValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringMustBeDigitWithFraction {
    String message() default "Amount must be digit with fraction {fraction}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int fraction();
}
