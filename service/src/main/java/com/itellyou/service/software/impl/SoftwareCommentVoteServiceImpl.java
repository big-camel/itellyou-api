package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareCommentVoteDao;
import com.itellyou.model.software.SoftwareCommentModel;
import com.itellyou.model.software.SoftwareCommentVoteModel;
import com.itellyou.model.event.SoftwareCommentEvent;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.VoteType;
import com.itellyou.service.software.SoftwareCommentSearchService;
import com.itellyou.service.software.SoftwareCommentService;
import com.itellyou.service.software.impl.SoftwareCommentVoteSearchServiceImpl;
import com.itellyou.service.common.VoteSearchService;
import com.itellyou.service.common.VoteService;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.util.DateUtils;
import com.itellyou.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.Map;

@CacheConfig(cacheNames = "software_comment_vote")
@Service
public class SoftwareCommentVoteServiceImpl implements VoteService<SoftwareCommentVoteModel> {

    private Logger logger = LoggerFactory.getLogger(SoftwareCommentVoteServiceImpl.class);

    private final SoftwareCommentVoteDao voteDao;
    private final SoftwareCommentService commentService;
    private final SoftwareCommentSearchService searchService;
    private final VoteSearchService<SoftwareCommentVoteModel> voteSearchService;
    private final OperationalPublisher operationalPublisher;

    @Autowired
    public SoftwareCommentVoteServiceImpl(SoftwareCommentVoteDao voteDao, SoftwareCommentService commentService, SoftwareCommentSearchService searchService, SoftwareCommentVoteSearchServiceImpl voteSearchService, OperationalPublisher operationalPublisher){
        this.voteDao = voteDao;
        this.commentService = commentService;
        this.searchService = searchService;
        this.voteSearchService = voteSearchService;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    public int insert(SoftwareCommentVoteModel voteModel) {
        RedisUtils.clear("software_comment_vote_" + voteModel.getCreatedUserId());
        return voteDao.insert(voteModel);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#commentId).concat('-').concat(#userId)")
    public int deleteByTargetIdAndUserId(Long commentId, Long userId) {
        RedisUtils.clear("software_comment_vote_" + userId);
        return voteDao.deleteByCommentIdAndUserId(commentId,userId);
    }

    @Override
    @CacheEvict(key = "T(String).valueOf(#id).concat('-').concat(#userId)")
    @Transactional
    public Map<String, Object> doVote(VoteType type, Long id, Long userId, Long ip) {
        try{
            SoftwareCommentVoteModel voteModel = voteSearchService.findByTargetIdAndUserId(id,userId);

            if(voteModel != null){
                int result = deleteByTargetIdAndUserId(id,userId);
                if(result != 1) throw new Exception("删除Vote失败");
                result = commentService.updateVote(voteModel.getType(),-1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }
            if(voteModel == null || !voteModel.getType().equals(type)){
                voteModel = new SoftwareCommentVoteModel(id,type, DateUtils.getTimestamp(),userId, ip);
                int result = insert(voteModel);
                if(result != 1) throw new Exception("写入Vote失败");
                result = commentService.updateVote(type,1,id);
                if(result != 1) throw new Exception("更新Vote失败");
            }

            SoftwareCommentModel commentModel = searchService.findById(id);
            if(commentModel == null) throw new Exception("获取评论失败");
            if(commentModel.getCreatedUserId().equals((userId))) throw new Exception("不能给自己点赞");
            Map<String,Object> data = new HashMap<>();
            data.put("id",commentModel.getId());
            data.put("parentId",commentModel.getParentId());
            data.put("support",commentModel.getSupport());
            data.put("oppose",commentModel.getOppose());
            operationalPublisher.publish(new SoftwareCommentEvent(this,type.equals(VoteType.SUPPORT) ? EntityAction.LIKE : EntityAction.UNLIKE,commentModel.getId(),commentModel.getCreatedUserId(),userId, DateUtils.getTimestamp(),ip));
            RedisUtils.clear("software_comment_vote_" + userId);
            return data;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
