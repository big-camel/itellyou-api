package com.itellyou.service.event;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.AnswerCommentEvent;
import com.itellyou.model.event.AnswerEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.user.UserActivityModel;
import com.itellyou.service.common.IndexManagerService;
import com.itellyou.service.user.UserActivityService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AnswerEventListener {

    private final UserActivityService activityService;
    private final IndexManagerService indexManagerService;

    public AnswerEventListener(UserActivityService activityService, IndexManagerService indexManagerService) {
        this.activityService = activityService;
        this.indexManagerService = indexManagerService;
    }

    @EventListener
    @Async
    public void answerEvent(AnswerEvent event){
        OperationalModel model = event.getOperationalModel();
        if(model == null) return;

        switch (model.getAction()){
            case LIKE:// 点赞
            case UNLIKE:// 取消点赞
            case DISLIKE:// 反对
            case UNDISLIKE:// 取消反对
            case FOLLOW:// 收藏
            case UNFOLLOW:// 取消收藏
            case PUBLISH:// 发布
            case UPDATE:// 更新
            case COMMENT:// 评论
            case VIEW:// 查看
            case REVERT:// 撤销删除
                // 更新索引
                indexManagerService.put(new IndexQueueModel(model.getType(),model.getTargetId()));
                break;
            case DELETE:// 删除回答
                // 删除索引
                indexManagerService.put(new IndexQueueModel(model.getType(),true,model.getTargetId()));
                break;
        }
        // 写入用户活动记录
        switch (model.getAction()){
            case LIKE:// 点赞
            case FOLLOW:// 关注
            case PUBLISH:// 发布
                // 有相同操作则更新用户活动时间
                activityService.insert(new UserActivityModel(model.getAction(),model.getType(),model.getTargetId(),model.getTargetUserId(),model.getCreatedUserId(),model.getCreatedTime(),model.getCreatedIp()));
                break;
            case UNFOLLOW:// 取消收藏 删除用户活动记录
                activityService.delete(EntityAction.FOLLOW,model.getType(),model.getTargetId(),model.getCreatedUserId());
                break;
            case UNLIKE:// 取消点赞 删除用户活动记录
                activityService.delete(EntityAction.LIKE,model.getType(),model.getTargetId(),model.getCreatedUserId());
                break;
        }
    }

    @EventListener
    @Async
    public void commentEvent(AnswerCommentEvent event){
        OperationalModel model = event.getOperationalModel();
        // 回答有新评论的时候更新回答索引
        switch (model.getAction()){
            case PUBLISH:// 新增评论
            case COMMENT:// 评论已有的评论
                indexManagerService.put(new IndexQueueModel(model.getType(),model.getTargetId()));
                break;
        }
    }
}
