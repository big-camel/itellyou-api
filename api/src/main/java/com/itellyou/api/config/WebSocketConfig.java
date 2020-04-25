package com.itellyou.api.config;

import com.itellyou.api.handler.NotificationSocketHandler;
import com.itellyou.api.handler.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationSocketHandler notificationSocketHandler;
    private final WebSocketHandshakeInterceptor handshakeInterceptor;

    @Autowired
    public WebSocketConfig(WebSocketHandshakeInterceptor handshakeInterceptor,NotificationSocketHandler notificationSocketHandler){
        this.handshakeInterceptor = handshakeInterceptor;
        this.notificationSocketHandler = notificationSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(notificationSocketHandler, "/websocket").addInterceptors(handshakeInterceptor).setAllowedOrigins("*");
    }
}

