package com.fjb.sunrise.constraints;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class StringMustBeDigitWithFractionValidator
    implements ConstraintValidator<StringMustBeDigitWithFraction, String> {
    private int fraction = 0;

    @Override
    public void initialize(StringMustBeDigitWithFraction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        fraction = constraintAnnotation.fraction();
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        String ignoreComma = string.replaceAll(",", "");
        String ignoreDot = ignoreComma.replace(".", "");
        if (ignoreDot.chars().allMatch(Character::isDigit)) {
            if (fraction > 0) {
                int startOfFraction = ignoreComma.indexOf(".");
                if(startOfFraction==-1) {
                    return true;
                }
                if (ignoreComma.length() - startOfFraction - 1 > fraction) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
