package com.itellyou.service.user;

import java.util.function.BiConsumer;

public interface UserNotificationBatchService {

    void start(Long userId, BiConsumer callback);

    void stop(Long userId);

    void doStart(Long userId, BiConsumer callback);
}
