package com.itellyou.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itellyou.model.common.NotificationGroupCountModel;
import com.itellyou.model.event.NotificationEvent;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.NotificationManagerService;
import com.itellyou.service.common.NotificationSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationSocketServiceImpl extends AbstractWebSocketHandler implements NotificationSocketService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 用户 WebSocketSession Map
     */
    private final Map<Long, List<WebSocketSession>> socketMap = new HashMap();

    private final NotificationManagerService managerService;

    /**
     * 单用户最高可以连接多少个 WebSocketSession
     */
    private final int signMax = 10;

    public NotificationSocketServiceImpl(NotificationManagerService managerService) {
        this.managerService = managerService;
    }

    @Override
    public UserInfoModel getUser(WebSocketSession session) {
        Map<String,Object> attributes = session.getAttributes();
        UserInfoModel userModel = null;
        String userKey = "user";
        if(attributes.containsKey(userKey)){
            userModel = (UserInfoModel)attributes.get(userKey);
        }
        return userModel;
    }

    @Override
    public void ready(WebSocketSession session) {
        try {
            UserInfoModel userModel = getUser(session);
            if(userModel == null) {
                session.close();
                return;
            }
            List<WebSocketSession> socketSessions = socketMap.get(userModel.getId());
            if(socketSessions == null || socketSessions.size() == 1){
                managerService.addJob(userModel.getId());
            }else{
                managerService.sendEvent(userModel.getId());
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @EventListener
    @Override
    @Async
    public void listenerEvent(NotificationEvent event) {
        List<NotificationGroupCountModel> groupCountModels = event.getGroupCountModels();
        Long userId = event.getUserId();
        if(userId != null && groupCountModels != null){
            List<WebSocketSession> socketSessions = socketMap.get(userId);

            int count = 0;
            for (NotificationGroupCountModel groupCountModel:groupCountModels) {
                count += groupCountModel.getCount();
            }
            if(count == 0) return;

            JSONObject groupObject = new JSONObject();
            groupObject.put("count",count);
            groupObject.put("group",groupCountModels);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("notifications",groupObject);

            for (WebSocketSession socketSession:socketSessions) {
                if(socketSession != null && socketSession.isOpen()){
                    try {
                        socketSession.sendMessage(new TextMessage(jsonObject.toJSONString()));
                    } catch (IOException e) {
                        logger.error(e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserInfoModel userModel = getUser(session);
        if(userModel == null) {
            session.close();
            return;
        }
        if(socketMap.containsKey(userModel.getId())){
            List<WebSocketSession> socketSessions = socketMap.get(userModel.getId());
            if(socketSessions.size() >= signMax) {
                session.close();
                return;
            }
            socketSessions.add(session);
        }else{
            List<WebSocketSession> socketSessions = new ArrayList<>();
            socketSessions.add(session);
            socketMap.put(userModel.getId(),socketSessions);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserInfoModel userModel = getUser(session);
        if(userModel == null) return;
        List<WebSocketSession> socketSessions = socketMap.get(userModel.getId());
        if(session != null && socketSessions != null){
            socketSessions.remove(session);
            if(socketSessions.size() == 0){
                managerService.removeJob(userModel.getId());
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JSONObject json = JSON.parseObject(message.getPayload());
            if(json != null){
                String action = json.getString("action");
                if(action.equals("ready")){
                    ready(session);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}
