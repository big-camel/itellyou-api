package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareCommentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.software.SoftwareInfoModel;
import com.itellyou.model.event.SoftwareCommentEvent;
import com.itellyou.model.event.SoftwareEvent;
import com.itellyou.model.event.OperationalEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.software.SoftwareCommentService;
import com.itellyou.service.software.SoftwareInfoService;
import com.itellyou.service.software.SoftwareSingleService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@CacheConfig(cacheNames = CacheKeys.SOFTWARE_COMMENT_KEY)
@Service
public class SoftwareCommentServiceImpl implements SoftwareCommentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SoftwareCommentDao commentDao;
    private final SoftwareInfoService softwareService;
    private final SoftwareSingleService searchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public SoftwareCommentServiceImpl(SoftwareCommentDao commentDao, SoftwareInfoService softwareService, SoftwareSingleService searchService, OperationalPublisher operationalPublisher){
        this.commentDao = commentDao;
        this.softwareService = softwareService;
        this.searchService = searchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#parentId")
    public SoftwareCommentModel insert(Long softwareId, Long parentId, Long replyId, String content, String html, Long userId, Long ip,Boolean sendEvent) throws Exception {
        try{
            SoftwareInfoModel softwareModel = searchService.findById(softwareId);
            if(softwareModel == null) throw new Exception("错误的文章ID");
            SoftwareCommentModel replyCommentModel = null;
            if(replyId != null && !replyId.equals(0l)){
                replyCommentModel = commentDao.findById(replyId);
                if(replyCommentModel == null) throw new Exception("错误的回复ID");
            }

            SoftwareCommentModel commentModel = new SoftwareCommentModel(null,softwareId,parentId,replyId,false,content,html,0,0,0, DateUtils.toLocalDateTime(),userId, ip,null,null,null);
            int result = commentDao.insert(commentModel);

            if(result != 1) throw new Exception("写入评论失败");
            if(parentId != 0){
                result = updateComments(parentId,1);
                if(result != 1) throw new Exception("更新父级子评论数失败");
            }
            result = softwareService.updateComments(softwareId,1);
            if(result != 1) throw new Exception("更新软件评论数失败");

            EntityType notificationType = EntityType.SOFTWARE;
            Long targetUserId = softwareModel.getCreatedUserId();
            if(replyCommentModel != null){
                notificationType = EntityType.SOFTWARE_COMMENT;
                targetUserId = replyCommentModel.getCreatedUserId();
            }
            if(sendEvent == true) {
                OperationalEvent event = notificationType.equals(EntityType.SOFTWARE) ?

                        new SoftwareEvent(this, EntityAction.COMMENT,
                                commentModel.getId(), targetUserId, userId, DateUtils.toLocalDateTime(), ip) :

                        new SoftwareCommentEvent(this, EntityAction.COMMENT, commentModel.getId(), targetUserId, userId, DateUtils.toLocalDateTime(), ip);
                operationalPublisher.publish(event);
            }
            return commentModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateDeleted(Long id, Boolean isDeleted,Long userId,Long ip) {
        SoftwareCommentModel commentModel = commentDao.findById(id);
        if(commentModel == null) return 0;
        if(!commentModel.getCreatedUserId().equals(userId)) return 0;
        int result = commentDao.updateDeleted(id,isDeleted);
        if(result != 1) return 0;
        operationalPublisher.publish(new SoftwareCommentEvent(this, isDeleted ? EntityAction.DELETE : EntityAction.REVERT,
                commentModel.getId(),commentModel.getCreatedUserId(),userId,DateUtils.toLocalDateTime(),ip));
        return 1;
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateComments(Long id, Integer value) {
        return commentDao.updateComments(id,value);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateVote(VoteType type, Integer value, Long id) {
        return commentDao.updateVote(type,value,id);
    }
}
