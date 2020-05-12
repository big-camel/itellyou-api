package com.itellyou.service.event;

import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.service.common.OperationalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationalPublisher {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationContext applicationContext;
    private final OperationalService operationalService;

    public OperationalPublisher(ApplicationContext applicationContext, OperationalService operationalService) {
        this.applicationContext = applicationContext;
        this.operationalService = operationalService;
    }

    @Async
    public void publish(OperationalEvent event){
        OperationalModel model = event.getOperationalModel();
        if(model != null && model.getTargetId() != null){
            int result = operationalService.insert(model);
            if(result != 1) {
                logger.error("写入操作日志失败,{}", event.toString());
                return;
            }
        }
        applicationContext.publishEvent(event);
        logger.info("发布了事件：",event);
    }
}
