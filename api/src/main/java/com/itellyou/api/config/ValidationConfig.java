package com.itellyou.api.config;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ValidationConfig {

    @Bean
    public static Validator validator() {

        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        Map<String,String > propertyMap = new HashMap<>();
        propertyMap.put(HibernateValidatorConfiguration.FAIL_FAST,"true");
        localValidatorFactoryBean.setValidationPropertyMap(propertyMap);

        return localValidatorFactoryBean;
    }
}
