package com.itellyou.service.event;

import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.common.NotificationManagerService;
import com.itellyou.service.user.UserBankService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GlobalEventListener {

    private final UserBankService bankService;
    private final NotificationManagerService notificationManagerService;

    public GlobalEventListener(UserBankService bankService, NotificationManagerService notificationManagerService) {
        this.bankService = bankService;
        this.notificationManagerService = notificationManagerService;
    }

    @EventListener
    @Async
    public void globalEvent(OperationalEvent event){
        OperationalModel model = event.getOperationalModel();
        // 更新积分
        bankService.updateByOperational(UserBankType.CREDIT,model);
        // 更新权限分
        bankService.updateByOperational(UserBankType.SCORE,model);
        // 设置消息
        notificationManagerService.put(model);
    }
}
