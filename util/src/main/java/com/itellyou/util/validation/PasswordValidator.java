package com.itellyou.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password,String> {
    private String passwordReg = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)]|[~`!@#$%^&*()\\\\_+\\-={}\\[\\]|'\";:,./<>?])+$)([^(0-9a-zA-Z)]|[~`!@#$%^&*()\\\\_+\\-={}\\[\\]|'\";:,./<>?]|[a-z]|[A-Z]|[0-9])+$";
    private Pattern passwordPattern = Pattern.compile(passwordReg);

    private int min = 6;
    private int max = 20;

    @Override
    public void initialize(Password constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) return  true;
        int length = value.length();
        return passwordPattern.matcher(value).matches() && length >= min && length <= max;
    }
}
