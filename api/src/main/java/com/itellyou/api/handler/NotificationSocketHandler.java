package com.itellyou.api.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserNotificationGroupCountModel;
import com.itellyou.service.user.UserNotificationBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class NotificationSocketHandler extends AbstractWebSocketHandler {

    private final UserNotificationBatchService batchService;

    @Autowired
    public NotificationSocketHandler(UserNotificationBatchService batchService){
        this.batchService = batchService;
    }

    private final Map<Long, List<WebSocketSession>> socketMap = new HashMap();

    public UserInfoModel getUser(WebSocketSession session){
        Map<String,Object> attributes = session.getAttributes();
        UserInfoModel userModel = null;
        String userKey = "user";
        if(attributes.containsKey(userKey)){
            userModel = (UserInfoModel)attributes.get(userKey);
        }
        return userModel;
    }

    public void ready(WebSocketSession session) throws IOException {
        BiConsumer callback = (userId,groupCounts) -> {
            List<WebSocketSession> socketSessions = socketMap.get(userId);
            if(groupCounts == null) return;
            List<UserNotificationGroupCountModel> groupCountModels = (List<UserNotificationGroupCountModel>)groupCounts;
            int count = 0;
            for (UserNotificationGroupCountModel groupCountModel:groupCountModels) {
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
                        e.printStackTrace();
                    }
                }
            }
        };
        UserInfoModel userModel = getUser(session);
        if(userModel == null) {
            session.close();
            System.out.println("user is null , start before , close");
            return;
        }
        batchService.start(userModel.getId(),callback);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserInfoModel userModel = getUser(session);
        if(userModel == null) {
            System.out.println("user is null , close");
            session.close();
            return;
        }
        if(socketMap.containsKey(userModel.getId())){
            List<WebSocketSession> socketSessions = socketMap.get(userModel.getId());
            if(socketSessions.size() >= 30) {
                System.out.println("sessions size >= 30 , close");
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
        System.out.println("sessions remove before , " + userModel.getId() + ":" + socketSessions.size());
        socketSessions.remove(session);
        System.out.println("sessions remove after , " + userModel.getId() + ":" + socketSessions.size());
        if(socketSessions.size() == 0){
            System.out.println("sessions size is 0 , stop ws");
            batchService.stop(userModel.getId());
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
            System.out.println(e);
        }
    }
}
