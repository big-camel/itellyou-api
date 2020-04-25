package com.itellyou.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= PathValidator.class)
public @interface Path {
    String message() default"错误的路径";

    int min() default 4;

    int max() default 50;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
