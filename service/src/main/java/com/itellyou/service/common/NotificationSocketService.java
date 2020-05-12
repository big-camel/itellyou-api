package com.itellyou.service.common;

import com.itellyou.model.event.NotificationEvent;
import com.itellyou.model.user.UserInfoModel;
import org.springframework.web.socket.WebSocketSession;

public interface NotificationSocketService {

    UserInfoModel getUser(WebSocketSession session);

    void ready(WebSocketSession session);

    void listenerEvent(NotificationEvent event);
}
