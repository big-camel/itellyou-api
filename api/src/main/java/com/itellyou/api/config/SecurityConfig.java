package com.itellyou.api.config;

import com.itellyou.api.handler.TokenAccessDeniedHandler;
import com.itellyou.api.handler.TokenAuthenticationProvider;
import com.itellyou.api.interceptor.TokenSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenSecurityInterceptor tokenSecurityInterceptor;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;

    @Autowired
    public SecurityConfig(TokenSecurityInterceptor tokenSecurityInterceptor, TokenAccessDeniedHandler tokenAccessDeniedHandler) {
        this.tokenSecurityInterceptor = tokenSecurityInterceptor;
        this.tokenAccessDeniedHandler = tokenAccessDeniedHandler;
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth,TokenAuthenticationProvider provider){
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        tokenSecurityInterceptor.setAuthenticationManager(authenticationManager());

        http.csrf().disable();
        // 拦截器，在 ExceptionTranslationFilter 异常过滤器后添加，否则无法捕获异常，详细解释：https://docs.spring.io/spring-security/site/docs/5.3.1.RELEASE/reference/html5/#servlet-exceptiontranslationfilter
        http.addFilterAfter(tokenSecurityInterceptor, ExceptionTranslationFilter.class);

        http.authorizeRequests()
                .anyRequest().authenticated().and()
        .httpBasic().and().exceptionHandling().accessDeniedHandler(tokenAccessDeniedHandler);
    }
}
