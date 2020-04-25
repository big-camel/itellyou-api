package com.itellyou.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MobileValidator implements ConstraintValidator<Mobile,String> {

    private String mobileReg = "^1\\d{10}$";
    private Pattern mobilePattern = Pattern.compile(mobileReg);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) return  true;
        return mobilePattern.matcher(value).matches();
    }
}
