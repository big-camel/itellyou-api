package com.itellyou.service.event;

import com.itellyou.model.event.NotificationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    private final ApplicationContext applicationContext;

    public NotificationPublisher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Async
    public void publish(NotificationEvent event){
        applicationContext.publishEvent(event);
    }
}
