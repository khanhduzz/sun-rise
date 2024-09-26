package com.fjb.sunrise.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AfterAndUntilNowValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterAndUntilNow {
    String message() default "Thời gian tạo phải sau 01/01/2020 và trước hiện tại";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value();
}
