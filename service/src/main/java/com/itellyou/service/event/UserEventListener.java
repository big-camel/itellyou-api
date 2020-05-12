package com.itellyou.service.event;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.UserEvent;
import com.itellyou.service.common.IndexManagerService;
import com.itellyou.service.user.UserActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserActivityService activityService;
    private final IndexManagerService indexManagerService;

    public UserEventListener(UserActivityService activityService, IndexManagerService indexManagerService) {
        this.activityService = activityService;
        this.indexManagerService = indexManagerService;
    }

    /**
     * 监听用户相关事件，更新索引
     * @param event
     */
    @EventListener
    @Async
    public void user(UserEvent event){
        OperationalModel model = event.getOperationalModel();
        if(model == null) return;

        switch (model.getAction()){
            case FOLLOW:// 关注
            case UNFOLLOW:// 取消关注
            case PUBLISH:// 发布
            case UPDATE:// 更新
            case VIEW:// 查看
            case REVERT:// 撤销删除
                // 更新索引
                indexManagerService.put(new IndexQueueModel(model.getType(),model.getTargetId()));
                break;
            case DELETE:// 删除标签
                // 删除索引
                indexManagerService.put(new IndexQueueModel(model.getType(),true,model.getTargetId()));
                break;
        }
    }
}
