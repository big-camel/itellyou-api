package com.itellyou.api.config;

import com.itellyou.api.interceptor.WebSocketHandshakeInterceptor;
import com.itellyou.service.common.NotificationSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationSocketService notificationSocketService;
    private final WebSocketHandshakeInterceptor handshakeInterceptor;

    @Autowired
    public WebSocketConfig(WebSocketHandshakeInterceptor handshakeInterceptor, NotificationSocketService notificationSocketService){
        this.handshakeInterceptor = handshakeInterceptor;
        this.notificationSocketService = notificationSocketService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler((WebSocketHandler) notificationSocketService, "/websocket").addInterceptors(handshakeInterceptor).setAllowedOrigins("*");
    }
}

