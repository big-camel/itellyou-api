package com.itellyou.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<Name,String> {

    private int min = 2;
    private int max = 60;

    @Override
    public void initialize(Name constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) return  true;
        int length = value.trim().length();
        return length >= min && length <= max;
    }
}
