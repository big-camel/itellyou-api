package com.itellyou.service.event;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.ColumnEvent;
import com.itellyou.model.event.ColumnIndexEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserActivityModel;
import com.itellyou.service.common.IndexManagerService;
import com.itellyou.service.user.UserActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ColumnEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserActivityService activityService;
    private final IndexManagerService indexManagerService;

    public ColumnEventListener(UserActivityService activityService, IndexManagerService indexManagerService) {
        this.activityService = activityService;
        this.indexManagerService = indexManagerService;
    }

    /**
     * 监听专栏相关事件，更新索引
     * @param event
     */
    @EventListener
    @Async
    public void column(ColumnEvent event){
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
            case DELETE:// 删除专栏
                // 删除索引
                indexManagerService.put(new IndexQueueModel(model.getType(),true,model.getTargetId()));
                break;
        }
        // 写入用户活动记录
        switch (model.getAction()){
            case FOLLOW:// 关注
                // 有相同操作则更新用户活动时间
                activityService.insert(new UserActivityModel(model.getAction(),model.getType(),model.getTargetId(),model.getTargetUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp()));
                break;
            case UNFOLLOW:// 取消收藏 删除用户活动记录
                activityService.delete(EntityAction.FOLLOW,model.getType(),model.getTargetId(),model.getCreatedUserId());
                break;
        }
    }

    /**
     * 更新专栏索引
     * @param event
     */
    @EventListener
    @Async
    public void indexEvent(ColumnIndexEvent event){
        HashSet<Long> ids = event.getIds();
        if(ids != null){
            indexManagerService.put(EntityType.COLUMN,ids);
        }
    }
}
