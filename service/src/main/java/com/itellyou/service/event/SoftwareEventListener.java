package com.itellyou.service.event;

import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.ArticleCommentEvent;
import com.itellyou.model.event.ArticleEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.user.UserActivityModel;
import com.itellyou.service.common.IndexManagerService;
import com.itellyou.service.user.UserActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 软件相关事件监听类
 */
@Service
public class SoftwareEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserActivityService activityService;
    private final IndexManagerService indexManagerService;

    public SoftwareEventListener(UserActivityService activityService, IndexManagerService indexManagerService) {
        this.activityService = activityService;
        this.indexManagerService = indexManagerService;
    }

    /**
     * 监听文章相关事件，更新文章索引
     * @param event
     */
    @EventListener
    @Async
    public void articleEvent(ArticleEvent event){
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
            case DELETE:// 删除文章
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

    /**
     * 软件评论新增时，更新文章索引
     * @param event
     */
    @EventListener
    @Async
    public void commentEvent(ArticleCommentEvent event){
        OperationalModel model = event.getOperationalModel();
        // 文章有新评论的时候更新文章索引
        switch (model.getAction()){
            case PUBLISH:
            case COMMENT:
                indexManagerService.put(new IndexQueueModel(model.getType(),model.getTargetId()));
                break;
        }
    }

}
