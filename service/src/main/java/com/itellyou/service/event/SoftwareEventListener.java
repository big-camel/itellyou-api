package com.itellyou.service.event;

import com.itellyou.model.common.DataUpdateQueueModel;
import com.itellyou.model.common.DataUpdateStepModel;
import com.itellyou.model.common.IndexQueueModel;
import com.itellyou.model.common.OperationalModel;
import com.itellyou.model.event.ArticleCommentEvent;
import com.itellyou.model.event.SoftwareEvent;
import com.itellyou.model.statistics.StatisticsIncomeQueueModel;
import com.itellyou.model.statistics.StatisticsIncomeStepModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserActivityModel;
import com.itellyou.service.common.DataUpdateManageService;
import com.itellyou.service.common.IndexManagerService;
import com.itellyou.service.statistics.StatisticsIncomeManageService;
import com.itellyou.service.user.UserActivityService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.Params;
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
    private final DataUpdateManageService dataUpdateManageService;
    private final StatisticsIncomeManageService incomeManageService;

    public SoftwareEventListener(UserActivityService activityService, IndexManagerService indexManagerService, DataUpdateManageService dataUpdateManageService, StatisticsIncomeManageService incomeManageService) {
        this.activityService = activityService;
        this.indexManagerService = indexManagerService;
        this.dataUpdateManageService = dataUpdateManageService;
        this.incomeManageService = incomeManageService;
    }

    /**
     * 监听软件相关事件，更新软件索引
     * @param event
     */
    @EventListener
    @Async
    public void softwareEvent(SoftwareEvent event){
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
        // 统计信息
        Long date = DateUtils.getTimestamp(model.getCreatedTime().toLocalDate());
        DataUpdateStepModel stepModel = new DataUpdateStepModel();
        stepModel.setId(model.getTargetId());
        switch (model.getAction()){
            case LIKE:
            case UNLIKE:// 取消点赞
                stepModel.setSupportStep(model.getAction().equals(EntityAction.UNLIKE) ? -1 : 1);
                break;
            case DISLIKE:
            case UNDISLIKE:// 取消反对
                stepModel.setOpposeStep(model.getAction().equals(EntityAction.UNDISLIKE) ? -1 : 1);
                break;
            case FOLLOW:
            case UNFOLLOW:
                stepModel.setStarStep(model.getAction().equals(EntityAction.UNFOLLOW) ? -1 : 1);
                break;
            case VIEW:
                stepModel.setViewStep(1);
                break;
            case COMMENT:
                stepModel.setCommentStep(1);
                break;
            case REWARD:
                //统计用户打赏收益
                StatisticsIncomeStepModel incomeStepModel = new StatisticsIncomeStepModel();
                incomeStepModel.setUserId(model.getTargetUserId());
                Params params = new Params(event.getArgs());
                Double rewardAmount = params.getDouble("amount",0.00);
                if(rewardAmount <= 0) break;
                incomeStepModel.setTipStep(rewardAmount);
                StatisticsIncomeQueueModel incomeQueueModel = new StatisticsIncomeQueueModel(model.getTargetUserId(),date,incomeStepModel);
                incomeManageService.put(incomeQueueModel);
            default:
                stepModel = null;
        }
        if(stepModel != null){
            DataUpdateQueueModel queueModel = new DataUpdateQueueModel(model.getTargetUserId(), EntityType.SOFTWARE,date,stepModel);
            dataUpdateManageService.put(queueModel);
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
                // 统计信息
                Long date = DateUtils.getTimestamp(model.getCreatedTime().toLocalDate());
                DataUpdateStepModel stepModel = new DataUpdateStepModel();
                stepModel.setId(model.getTargetId());
                stepModel.setCommentStep(1);
                DataUpdateQueueModel queueModel = new DataUpdateQueueModel(model.getTargetUserId(), EntityType.SOFTWARE,date,stepModel);
                dataUpdateManageService.put(queueModel);

                indexManagerService.put(new IndexQueueModel(model.getType(),model.getTargetId()));
                break;
        }
    }

}
