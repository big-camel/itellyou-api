package com.itellyou.api.handler;

import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HandlerMethodInterceptor extends HandlerInterceptorAdapter {

    private final UserSearchService userSearchService;

    @Autowired
    public HandlerMethodInterceptor(UserSearchService userSearchService){
        this.userSearchService = userSearchService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(handler instanceof HandlerMethod){
            Cookie cookie = CookieUtils.getCookie(request,"token");
            if(cookie != null){
                String token = cookie.getValue();
                if(StringUtils.isNotEmpty(token)){
                    request.setAttribute("token",token);
                    UserInfoModel user = userSearchService.findByToken(token);
                    if(user != null){
                        request.setAttribute("user",user);
                    }
                }
            }
        }

        return true;
    }
}
