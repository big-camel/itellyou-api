package com.itellyou.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= NameValidator.class)
public @interface Name {
    String message() default"错误的名称";

    int min() default 2;

    int max() default 60;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
