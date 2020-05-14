package com.itellyou.api.interceptor;

import com.itellyou.api.handler.TokenAccessDecisionManager;
import com.itellyou.api.handler.TokenAuthenticationToken;
import com.itellyou.api.handler.TokenSecurityMetadataSource;
import com.itellyou.util.CookieUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TokenSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private final TokenSecurityMetadataSource tokenSecurityMetadataSource;

    @Autowired
    public TokenSecurityInterceptor(TokenAccessDecisionManager tokenAccessDecisionManager, TokenSecurityMetadataSource tokenSecurityMetadataSource) {
        this.tokenSecurityMetadataSource = tokenSecurityMetadataSource;
        super.setAccessDecisionManager(tokenAccessDecisionManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);
    }

    public void invoke(FilterInvocation fi) throws IOException, ServletException {
        HttpServletRequest request = fi.getHttpRequest();
        // 获取User Token
        Cookie cookie = CookieUtils.getCookie(request,"token");
        String userToken = cookie != null ? cookie.getValue() : null;
        if(StringUtils.isEmpty(userToken)){
            // cookie 中如果没有，就从参数中找
            userToken = request.getParameter("token");
        }
        // 设置验证模块
        SecurityContextHolder.getContext().setAuthentication(new TokenAuthenticationToken(userToken));

        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(request, fi.getResponse());
        }finally {
            super.afterInvocation(token, null);
        }
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return tokenSecurityMetadataSource;
    }
}
