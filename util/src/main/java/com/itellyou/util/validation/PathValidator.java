package com.itellyou.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PathValidator implements ConstraintValidator<Path,String> {

    private int min = 4;
    private int max = 50;

    private String pathReg = "^[a-zA-z0-9_.]{4,50}$";
    private Pattern pathPattern = Pattern.compile(pathReg);

    @Override
    public void initialize(Path constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) return  true;
        int length = value.trim().length();
        return length >= min && length <= max && pathPattern.matcher(value).matches();
    }
}
