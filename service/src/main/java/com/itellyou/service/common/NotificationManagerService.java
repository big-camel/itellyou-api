package com.itellyou.service.common;

import com.itellyou.model.common.OperationalModel;

public interface NotificationManagerService {

    /**
     * 新增读取消息队列定时器
     * @param userId
     */
    void addJob(Long userId);

    /**
     * 移除读取消息队列定时器
     * @param userId
     */
    void removeJob(Long userId);

    /**
     * 新增一个操作消息放入队列
     * @param model
     */
    void put(OperationalModel model);

    /**
     * 触发一个事件发送消息数据给用户websocket
     * @param userId
     */
    void sendEvent(Long userId);
}
