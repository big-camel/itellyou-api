package com.itellyou.api.handler;

import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.CookieUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final UserSearchService userSearchService;

    @Autowired
    public WebSocketHandshakeInterceptor(UserSearchService userSearchService){
        this.userSearchService = userSearchService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes)
            throws Exception {

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        String key = "token";

        Cookie cookie = CookieUtils.getCookie(servletRequest, key);
        String token = cookie == null ? null : cookie.getValue();
        if (StringUtils.isEmpty(token)) {
            token = servletRequest.getParameter(key);
        }
        if(StringUtils.isEmpty(token)) return false;
        UserInfoModel infoModel = userSearchService.findByToken(token);
        if (infoModel == null || infoModel.isDisabled()) {
            return false;
        }
        attributes.put("user",infoModel);
        return super.beforeHandshake(request, response, webSocketHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception e) {
        super.afterHandshake(request, response, wsHandler, e);
    }
}
