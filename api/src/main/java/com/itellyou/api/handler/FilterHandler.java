package com.itellyou.api.handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName="FilterHandler",urlPatterns="/*")
@Configuration
public class FilterHandler implements Filter {
    // 缓存 request content ，以免多次读取IO报错
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        chain.doFilter(new ContentCachingRequestWrapper(request),servletResponse);
    }
}
